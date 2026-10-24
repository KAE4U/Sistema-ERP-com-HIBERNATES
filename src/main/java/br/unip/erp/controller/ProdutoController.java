package br.unip.erp.controller;

import br.unip.erp.dao.ProdutoDAO;
import br.unip.erp.model.Produto;
import br.unip.erp.service.*;
import java.util.List;

public class ProdutoController {
  private final ProdutoDAO dao = new ProdutoDAO();
  private final ProdutoService service = new ProdutoService();

  public int salvar(Produto p) {
    return service.salvar(p);
  }

  public void excluir(int id) {
    AutorizacaoService.exigirAdmin();
    dao.excluir(id);
  }

  public Produto buscar(int id) {
    return dao.buscarPorCodigo(id);
  }

  public List<Produto> listar() {
    return dao.listar();
  }
}
