package br.unip.erp.controller;

import br.unip.erp.dao.CompraDAO;
import br.unip.erp.model.Compra;
import br.unip.erp.service.CompraService;
import java.util.List;

public class CompraController {
  private final CompraDAO dao = new CompraDAO();
  private final CompraService service = new CompraService();

  public int registrar(Compra c) {
    return service.registrar(c);
  }

  public void excluir(int id) {
    service.excluir(id);
  }

  public List<Compra> listar() {
    return dao.listar();
  }

  public Compra buscar(int id) {
    return dao.buscarPorCodigo(id);
  }

  public void recalcularTotais(Compra c) {
    service.recalcular(c);
  }
}
