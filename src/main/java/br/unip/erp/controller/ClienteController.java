package br.unip.erp.controller;

import br.unip.erp.dao.ClienteDAO;
import br.unip.erp.model.*;
import br.unip.erp.service.AutorizacaoService;
import java.util.List;

public class ClienteController {
  private final ClienteDAO dao = new ClienteDAO();

  public int salvar(Cliente c) {
    if (c.getPessoa() == null
        || c.getPessoa().getNome() == null
        || c.getPessoa().getNome().isBlank())
      throw new IllegalArgumentException("O nome do cliente é obrigatório.");
    if (c.getCodigo() != 0) {
      Cliente atual = dao.buscarPorCodigo(c.getCodigo());
      if (atual == null) throw new IllegalArgumentException("Cliente não encontrado.");
      preservar(atual.getPessoa(), c.getPessoa());
    }
    return c.getCodigo() == 0 ? dao.inserir(c) : dao.atualizar(c).getCodigo();
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

  public Cliente buscar(int id) {
    return dao.buscarPorCodigo(id);
  }

  public List<Cliente> listar() {
    return dao.listar();
  }
}
