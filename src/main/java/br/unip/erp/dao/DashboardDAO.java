package br.unip.erp.dao;

import br.unip.erp.model.DashboardDTO;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.math.*;
import java.time.*;
import java.util.*;

public class DashboardDAO {
  public DashboardDTO carregar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      DashboardDTO d = new DashboardDTO();
      d.setFaturamento(
          decimal(
              em.createQuery("select coalesce(sum(v.total),0) from Venda v").getSingleResult()));
      d.setCustoCompras(
          decimal(
              em.createQuery("select coalesce(sum(c.total),0) from Compra c").getSingleResult()));
      d.setSaldo(d.getFaturamento().subtract(d.getCustoCompras()));
      d.setEstoqueMinimo(
          em.createQuery(
                  "select count(p) from Produto p where p.ativo='S' and p.estoque<=p.minimo",
                  Long.class)
              .getSingleResult());
      YearMonth inicio = YearMonth.now().minusMonths(11);
      for (int i = 0; i < 12; i++) {
        YearMonth m = inicio.plusMonths(i);
        d.getVendasMensais().put(m, BigDecimal.ZERO);
        d.getComprasMensais().put(m, BigDecimal.ZERO);
      }
      preencherMeses(
          em.createNativeQuery(
                  "select date_trunc('month',vda_data),coalesce(sum(vda_total),0) from venda where"
                      + " vda_data>=?1 group by 1 order by 1")
              .setParameter(1, inicio.atDay(1))
              .getResultList(),
          d.getVendasMensais());
      preencherMeses(
          em.createNativeQuery(
                  "select date_trunc('month',cpr_emissao),coalesce(sum(cpr_total),0) from compra"
                      + " where cpr_emissao>=?1 group by 1 order by 1")
              .setParameter(1, inicio.atDay(1))
              .getResultList(),
          d.getComprasMensais());
      for (Object[] r :
          em.createQuery(
                  "select p.formaPagamento.nome,coalesce(sum(p.valor),0) from VendaPagamento p"
                      + " group by p.formaPagamento.nome",
                  Object[].class)
              .getResultList()) d.getMixPagamentos().put((String) r[0], decimal(r[1]));
      return d;
    }
  }

  private void preencherMeses(List<?> rows, Map<YearMonth, BigDecimal> destino) {
    for (Object row : rows) {
      Object[] r = (Object[]) row;
      LocalDate data = converterData(r[0]);
      YearMonth mes = YearMonth.from(data);
      if (destino.containsKey(mes)) {
        destino.put(mes, decimal(r[1]));
      }
    }
  }

  private LocalDate converterData(Object valor) {
    if (valor instanceof java.sql.Timestamp timestamp) {
      return timestamp.toLocalDateTime().toLocalDate();
    }
    if (valor instanceof java.sql.Date data) {
      return data.toLocalDate();
    }
    if (valor instanceof LocalDate data) {
      return data;
    }
    if (valor instanceof LocalDateTime dataHora) {
      return dataHora.toLocalDate();
    }
    if (valor instanceof OffsetDateTime dataHora) {
      return dataHora.toLocalDate();
    }
    throw new IllegalStateException("Tipo de data inesperado no dashboard: " + valor);
  }

  private BigDecimal decimal(Object o) {
    return o == null
        ? BigDecimal.ZERO
        : o instanceof BigDecimal b ? b : new BigDecimal(o.toString());
  }
}
