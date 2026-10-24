package br.unip.erp.controller;

import br.unip.erp.dao.VendaDAO;
import br.unip.erp.model.Venda;
import br.unip.erp.service.VendaService;
import java.util.List;

public class VendaController {
  private final VendaDAO dao = new VendaDAO();
  private final VendaService service = new VendaService();

  public int registrar(Venda v) {
    return service.registrar(v);
  }

  public void excluir(int id) {
    service.excluir(id);
  }

  public List<Venda> listar() {
    return dao.listar();
  }

  public Venda buscar(int id) {
    return dao.buscarPorCodigo(id);
  }

  public void recalcularTotais(Venda v) {
    service.recalcular(v);
  }
}
