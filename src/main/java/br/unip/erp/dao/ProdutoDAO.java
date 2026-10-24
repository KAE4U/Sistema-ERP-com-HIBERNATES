package br.unip.erp.dao;

import br.unip.erp.model.Produto;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.*;
import java.util.List;

public class ProdutoDAO extends JpaGenericDAO<Produto, Integer> {
  public ProdutoDAO() {
    super(Produto.class);
  }

  @Override
  public List<Produto> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery("select p from Produto p order by p.nome", Produto.class)
          .getResultList();
    }
  }

  public Produto buscarComBloqueio(EntityManager em, int id) {
    return em.find(Produto.class, id, LockModeType.PESSIMISTIC_WRITE);
  }
}
