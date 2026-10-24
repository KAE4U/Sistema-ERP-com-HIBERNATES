package br.unip.erp.dao;

import br.unip.erp.model.Cliente;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ClienteDAO extends JpaGenericDAO<Cliente, Integer> {
  public ClienteDAO() {
    super(Cliente.class);
  }

  @Override
  public List<Cliente> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery(
              "select c from Cliente c join fetch c.pessoa p order by p.nome", Cliente.class)
          .getResultList();
    }
  }

  @Override
  public Cliente buscarPorCodigo(Integer id) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery(
              "select c from Cliente c join fetch c.pessoa where c.codigo=:id", Cliente.class)
          .setParameter("id", id)
          .getResultStream()
          .findFirst()
          .orElse(null);
    }
  }
}
