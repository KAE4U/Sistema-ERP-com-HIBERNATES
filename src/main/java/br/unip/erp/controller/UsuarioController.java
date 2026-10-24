package br.unip.erp.controller;

import br.unip.erp.dao.UsuarioDAO;
import br.unip.erp.model.Usuario;
import br.unip.erp.service.*;
import java.util.List;

public class UsuarioController {
  private final UsuarioDAO dao = new UsuarioDAO();
  private final UsuarioService service = new UsuarioService();

  public Usuario autenticar(String l, String s) {
    return l == null || l.isBlank() || s == null || s.isBlank()
        ? null
        : service.autenticar(l.trim(), s);
  }

  public int salvar(Usuario u) {
    validar(u, u.getCodigo() == 0);
    AutorizacaoService.exigirAdmin();
    return service.salvar(u);
  }

  public int cadastrarPublico(Usuario u) {
    validar(u, true);
    return service.cadastrarPublico(u);
  }

  public void excluir(int id) {
    AutorizacaoService.exigirAdmin();
    dao.excluir(id);
  }

  public Usuario buscar(int id) {
    return dao.buscarPorCodigo(id);
  }

  public List<Usuario> listar() {
    return dao.listar();
  }

  private void validar(Usuario u, boolean senhaObrigatoria) {
    if (u.getLogin() == null || u.getLogin().isBlank())
      throw new IllegalArgumentException("O login é obrigatório.");
    if (senhaObrigatoria && (u.getSenha() == null || u.getSenha().isBlank()))
      throw new IllegalArgumentException("A senha é obrigatória.");
  }
}
