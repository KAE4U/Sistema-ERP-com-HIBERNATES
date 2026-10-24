package br.unip.erp.dao;

import br.unip.erp.model.Pessoa;

public class PessoaDAO extends JpaGenericDAO<Pessoa, Integer> {
  public PessoaDAO() {
    super(Pessoa.class);
  }
}
