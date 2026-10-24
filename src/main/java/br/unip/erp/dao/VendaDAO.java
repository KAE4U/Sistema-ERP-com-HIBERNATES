package br.unip.erp.dao;

import br.unip.erp.model.Venda;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class VendaDAO extends JpaGenericDAO<Venda, Integer> {
  public VendaDAO() {
    super(Venda.class);
  }

  @Override
  public List<Venda> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery(
              "select v from Venda v join fetch v.cliente c join fetch c.pessoa order by v.codigo"
                  + " desc",
              Venda.class)
          .getResultList();
    }
  }

  @Override
  public Venda buscarPorCodigo(Integer id) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      Venda v =
          em.createQuery(
                  "select v from Venda v join fetch v.cliente c join fetch c.pessoa join fetch"
                      + " v.usuario left join fetch v.itens i left join fetch i.produto where"
                      + " v.codigo=:id",
                  Venda.class)
              .setParameter("id", id)
              .getResultStream()
              .findFirst()
              .orElse(null);
      if (v != null)
        v.getPagamentos()
            .forEach(
                p -> {
                  p.getFormaPagamento().getNome();
                });
      return v;
    }
  }
}
