package br.unip.erp.controller;

import br.unip.erp.dao.FornecedorDAO;
import br.unip.erp.model.*;
import br.unip.erp.service.AutorizacaoService;
import java.util.List;

public class FornecedorController {
  private final FornecedorDAO dao = new FornecedorDAO();

  public int salvar(Fornecedor f) {
    if (f.getPessoa() == null
        || f.getPessoa().getNome() == null
        || f.getPessoa().getNome().isBlank())
      throw new IllegalArgumentException("O nome do fornecedor é obrigatório.");
    if (f.getCodigo() != 0) {
      Fornecedor atual = dao.buscarPorCodigo(f.getCodigo());
      if (atual == null) throw new IllegalArgumentException("Fornecedor não encontrado.");
      preservar(atual.getPessoa(), f.getPessoa());
    }
    return f.getCodigo() == 0 ? dao.inserir(f) : dao.atualizar(f).getCodigo();
  }

  private void preservar(Pessoa a, Pessoa n) {
    n.setFantasia(a.getFantasia());
    n.setRgIe(a.getRgIe());
    n.setCadastro(a.getCadastro());
    n.setEndereco(a.getEndereco());
    n.setNumero(a.getNumero());
    n.setComplemento(a.getComplemento());
    n.setBairro(a.getBairro());
    n.setCep(a.getCep());
    n.setFone2(a.getFone2());
    n.setCelular(a.getCelular());
    n.setSite(a.getSite());
    n.setAtivo(a.getAtivo());
  }

  public void excluir(int id) {
    AutorizacaoService.exigirAdmin();
    dao.excluir(id);
  }

  public Fornecedor buscar(int id) {
    return dao.buscarPorCodigo(id);
  }

  public List<Fornecedor> listar() {
    return dao.listar();
  }
}
