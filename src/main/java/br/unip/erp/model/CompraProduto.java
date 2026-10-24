package br.unip.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "compra_produto")
public class CompraProduto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cpp_codigo")
  private Integer codigo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cpr_codigo", nullable = false)
  private Compra compra;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pro_codigo", nullable = false)
  private Produto produto;

  @Column(name = "cpp_qtde", nullable = false, precision = 14, scale = 4)
  private BigDecimal qtde = BigDecimal.ZERO;

  @Column(name = "cpp_preco", nullable = false, precision = 18, scale = 2)
  private BigDecimal preco = BigDecimal.ZERO;

  @Column(name = "cpp_desconto", precision = 18, scale = 2)
  private BigDecimal desconto = BigDecimal.ZERO;

  @Column(name = "cpp_total", precision = 18, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;

  public int getCodigo() {
    return codigo == null ? 0 : codigo;
  }

  public void setCodigo(int v) {
    codigo = v == 0 ? null : v;
  }

  public int getCompraCodigo() {
    return compra == null ? 0 : compra.getCodigo();
  }

  public void setCompraCodigo(int v) {}

  public Compra getCompra() {
    return compra;
  }

  public void setCompra(Compra v) {
    compra = v;
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
