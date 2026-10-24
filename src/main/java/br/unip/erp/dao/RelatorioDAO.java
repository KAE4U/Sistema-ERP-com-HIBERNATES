package br.unip.erp.dao;

import br.unip.erp.model.RelatorioLinha;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.math.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RelatorioDAO {
  private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public List<RelatorioLinha> vendasPorPeriodo(LocalDate ini, LocalDate fim) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      List<Object[]> rs =
          em.createQuery(
                  "select v.data,count(v),coalesce(sum(v.total),0) from Venda v where v.data"
                      + " between :i and :f group by v.data order by v.data",
                  Object[].class)
              .setParameter("i", ini)
              .setParameter("f", fim)
              .getResultList();
      List<RelatorioLinha> out = new ArrayList<>();
      for (Object[] r : rs)
        out.add(
            new RelatorioLinha(
                ((LocalDate) r[0]).format(DATA),
                BigDecimal.valueOf((Long) r[1]),
                (BigDecimal) r[2]));
      return out;
    }
  }

  public List<RelatorioLinha> produtosMaisVendidos(int limite) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      List<Object[]> rs =
          em.createQuery(
                  "select i.produto.nome,coalesce(sum(i.qtde),0),coalesce(sum(i.total),0) from"
                      + " VendaProduto i group by i.produto.codigo,i.produto.nome order by"
                      + " sum(i.qtde) desc,sum(i.total) desc",
                  Object[].class)
              .setMaxResults(limite)
              .getResultList();
      List<RelatorioLinha> out = new ArrayList<>();
      for (Object[] r : rs)
        out.add(new RelatorioLinha((String) r[0], (BigDecimal) r[1], (BigDecimal) r[2]));
      return out;
    }
  }
}
