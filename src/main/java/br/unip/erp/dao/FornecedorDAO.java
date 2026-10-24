package br.unip.erp.dao;

import br.unip.erp.model.Fornecedor;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class FornecedorDAO extends JpaGenericDAO<Fornecedor, Integer> {
  public FornecedorDAO() {
    super(Fornecedor.class);
  }

  @Override
  public List<Fornecedor> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery(
              "select f from Fornecedor f join fetch f.pessoa p order by p.nome", Fornecedor.class)
          .getResultList();
    }
  }

  @Override
  public Fornecedor buscarPorCodigo(Integer id) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery(
              "select f from Fornecedor f join fetch f.pessoa where f.codigo=:id", Fornecedor.class)
          .setParameter("id", id)
          .getResultStream()
          .findFirst()
          .orElse(null);
    }
  }
}
