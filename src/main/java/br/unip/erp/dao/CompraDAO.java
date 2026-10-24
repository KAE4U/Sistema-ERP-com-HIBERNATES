package br.unip.erp.dao;

import br.unip.erp.model.Compra;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CompraDAO extends JpaGenericDAO<Compra, Integer> {
  public CompraDAO() {
    super(Compra.class);
  }

  @Override
  public List<Compra> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery(
              "select c from Compra c join fetch c.fornecedor f join fetch f.pessoa order by"
                  + " c.codigo desc",
              Compra.class)
          .getResultList();
    }
  }

  @Override
  public Compra buscarPorCodigo(Integer id) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery(
              "select distinct c from Compra c join fetch c.fornecedor f join fetch f.pessoa join"
                  + " fetch c.usuario left join fetch c.itens i left join fetch i.produto where"
                  + " c.codigo=:id",
              Compra.class)
          .setParameter("id", id)
          .getResultStream()
          .findFirst()
          .orElse(null);
    }
  }
}
