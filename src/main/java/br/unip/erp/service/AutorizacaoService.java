package br.unip.erp.service;

import br.unip.erp.dao.UsuarioDAO;
import br.unip.erp.model.Usuario;
import br.unip.erp.util.Sessao;

/** Regras de autorização reforçadas por consulta ao banco. */
public final class AutorizacaoService {

  private AutorizacaoService() {}

  /**
   * Exige uma sessão ativa com perfil administrador. O usuário é recarregado do banco para que
   * exclusão, desativação ou rebaixamento de perfil revoguem a permissão imediatamente.
   */
  public static void exigirAdmin() {
    Usuario usuarioSessao = Sessao.getUsuarioLogado();
    if (usuarioSessao == null || usuarioSessao.getCodigo() == 0) {
      throw new SecurityException("Operação permitida apenas para administradores.");
    }

    Usuario usuarioAtual = new UsuarioDAO().buscarPorCodigo(usuarioSessao.getCodigo());
    boolean autorizado =
        usuarioAtual != null
            && "S".equalsIgnoreCase(usuarioAtual.getAtivo())
            && usuarioAtual.isAdmin();

    if (!autorizado) {
      Sessao.encerrar();
      throw new SecurityException(
          "Sua sessão não possui mais permissão de administrador. Faça o login novamente.");
    }

    usuarioAtual.setSenha(null);
    Sessao.setUsuarioLogado(usuarioAtual);
  }
}
