package br.unip.erp.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

/** Dados agregados do dashboard; não é entidade. */
public class DashboardDTO {
  private BigDecimal faturamento = BigDecimal.ZERO,
      custoCompras = BigDecimal.ZERO,
      saldo = BigDecimal.ZERO;
  private long estoqueMinimo;
  private final Map<YearMonth, BigDecimal> vendasMensais = new LinkedHashMap<>(),
      comprasMensais = new LinkedHashMap<>();
  private final Map<String, BigDecimal> mixPagamentos = new LinkedHashMap<>();

  public BigDecimal getFaturamento() {
    return faturamento;
  }

  public void setFaturamento(BigDecimal v) {
    faturamento = v;
  }

  public BigDecimal getCustoCompras() {
    return custoCompras;
  }

  public void setCustoCompras(BigDecimal v) {
    custoCompras = v;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal v) {
    saldo = v;
  }

  public long getEstoqueMinimo() {
    return estoqueMinimo;
  }

  public void setEstoqueMinimo(long v) {
    estoqueMinimo = v;
  }

  public Map<YearMonth, BigDecimal> getVendasMensais() {
    return vendasMensais;
  }

  public Map<YearMonth, BigDecimal> getComprasMensais() {
    return comprasMensais;
  }

  public Map<String, BigDecimal> getMixPagamentos() {
    return mixPagamentos;
  }
}
