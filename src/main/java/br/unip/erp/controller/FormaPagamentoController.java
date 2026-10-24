package br.unip.erp.controller;

import br.unip.erp.dao.FormaPagamentoDAO;
import br.unip.erp.model.FormaPagamento;
import br.unip.erp.service.AutorizacaoService;
import java.util.List;

public class FormaPagamentoController {
  private final FormaPagamentoDAO dao = new FormaPagamentoDAO();

  public int salvar(FormaPagamento f) {
    if (f.getNome() == null || f.getNome().isBlank())
      throw new IllegalArgumentException("O nome da forma de pagamento é obrigatório.");
    return f.getCodigo() == 0 ? dao.inserir(f) : dao.atualizar(f).getCodigo();
  }

  public void excluir(int id) {
    AutorizacaoService.exigirAdmin();
    dao.excluir(id);
  }

  public FormaPagamento buscar(int id) {
    return dao.buscarPorCodigo(id);
  }

  public List<FormaPagamento> listar() {
    return dao.listar();
  }
}
