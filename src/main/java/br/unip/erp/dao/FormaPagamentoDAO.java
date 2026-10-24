package br.unip.erp.dao;

import br.unip.erp.model.FormaPagamento;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class FormaPagamentoDAO extends JpaGenericDAO<FormaPagamento, Integer> {
  public FormaPagamentoDAO() {
    super(FormaPagamento.class);
  }

  @Override
  public List<FormaPagamento> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery("select f from FormaPagamento f order by f.nome", FormaPagamento.class)
          .getResultList();
    }
  }
}
