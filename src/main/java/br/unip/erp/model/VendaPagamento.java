package br.unip.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "venda_pagto")
public class VendaPagamento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vdp_codigo")
  private Integer codigo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "vda_codigo", nullable = false)
  private Venda venda;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "fpg_codigo", nullable = false)
  private FormaPagamento formaPagamento;

  @Column(name = "vdp_valor", precision = 18, scale = 2)
  private BigDecimal valor = BigDecimal.ZERO;

  public int getCodigo() {
    return codigo == null ? 0 : codigo;
  }

  public void setCodigo(int v) {
    codigo = v == 0 ? null : v;
  }

  public int getVendaCodigo() {
    return venda == null ? 0 : venda.getCodigo();
  }

  public void setVendaCodigo(int v) {}

  public Venda getVenda() {
    return venda;
  }

  public void setVenda(Venda v) {
    venda = v;
  }

  public FormaPagamento getFormaPagamento() {
    return formaPagamento;
  }

  public void setFormaPagamento(FormaPagamento v) {
    formaPagamento = v;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal v) {
    valor = v;
  }
}
