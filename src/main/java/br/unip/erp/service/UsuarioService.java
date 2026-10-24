package br.unip.erp.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.unip.erp.dao.DAOException;
import br.unip.erp.dao.UsuarioDAO;
import br.unip.erp.model.Usuario;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.regex.Pattern;

/** Autenticação, migração de senhas legadas e cadastro seguro de usuários. */
public class UsuarioService {

  private static final Pattern BCRYPT_COMPLETO =
      Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

  private final UsuarioDAO dao = new UsuarioDAO();

  public Usuario autenticar(String login, String senha) {
    EntityManager em = JpaUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      Usuario usuario = dao.buscarPorLogin(em, login);
      if (usuario == null) {
        tx.commit();
        return null;
      }

      String senhaGravada = usuario.getSenha();
      boolean hashBcrypt = isHashBcrypt(senhaGravada);
      boolean senhaValida =
          hashBcrypt
              ? BCrypt.verifyer().verify(senha.toCharArray(), senhaGravada).verified
              : senha.equals(senhaGravada);

      // Banco legado: converte a senha em texto para BCrypt no primeiro login válido.
      if (senhaValida && !hashBcrypt) {
        usuario.setSenha(hash(senha));
      }

      tx.commit();
      if (!senhaValida) {
        return null;
      }

      // O objeto de sessão nunca precisa carregar o hash.
      usuario.setSenha(null);
      return usuario;
    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw new DAOException("Erro ao autenticar usuário: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public int salvar(Usuario usuario) {
    if (usuario.getCodigo() == 0) {
      usuario.setSenha(hashObrigatorio(usuario.getSenha()));
      return dao.inserir(usuario);
    }

    Usuario atual = dao.buscarPorCodigo(usuario.getCodigo());
    if (atual == null) {
      throw new IllegalArgumentException("Usuário não encontrado.");
    }

    usuario.setCadastro(atual.getCadastro());
    if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
      usuario.setSenha(atual.getSenha());
    } else {
      // Todo valor digitado na tela é uma nova senha, mesmo que comece com "$2".
      usuario.setSenha(hash(usuario.getSenha()));
    }

    dao.atualizar(usuario);
    return usuario.getCodigo();
  }

  /** Cadastro disponível na tela de login: sempre cria conta comum e ativa. */
  public int cadastrarPublico(Usuario usuario) {
    usuario.setCodigo(0);
    usuario.setAdmin("N");
    usuario.setAtivo("S");
    return salvar(usuario);
  }

  private boolean isHashBcrypt(String valor) {
    return valor != null && BCRYPT_COMPLETO.matcher(valor).matches();
  }

  private String hashObrigatorio(String senha) {
    if (senha == null || senha.isBlank()) {
      throw new IllegalArgumentException("A senha é obrigatória.");
    }
    return hash(senha);
  }

  private String hash(String senha) {
    return BCrypt.withDefaults().hashToString(12, senha.toCharArray());
  }
}
