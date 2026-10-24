package br.unip.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "venda_produto")
public class VendaProduto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vep_codigo")
  private Integer codigo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "vda_codigo", nullable = false)
  private Venda venda;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pro_codigo", nullable = false)
  private Produto produto;

  @Column(name = "vep_qtde", precision = 14, scale = 4)
  private BigDecimal qtde = BigDecimal.ZERO;

  @Column(name = "vep_preco", precision = 18, scale = 2)
  private BigDecimal preco = BigDecimal.ZERO;

  @Column(name = "vep_desconto", precision = 18, scale = 2)
  private BigDecimal desconto = BigDecimal.ZERO;

  @Column(name = "vep_total", precision = 18, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;

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

  public Produto getProduto() {
    return produto;
  }

  public void setProduto(Produto v) {
    produto = v;
  }

  public BigDecimal getQtde() {
    return qtde;
  }

  public void setQtde(BigDecimal v) {
    qtde = v;
  }

  public BigDecimal getPreco() {
    return preco;
  }

  public void setPreco(BigDecimal v) {
    preco = v;
  }

  public BigDecimal getDesconto() {
    return desconto;
  }

  public void setDesconto(BigDecimal v) {
    desconto = v;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal v) {
    total = v;
  }
}
