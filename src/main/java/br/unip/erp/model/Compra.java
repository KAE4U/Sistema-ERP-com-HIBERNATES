package br.unip.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "compra")
public class Compra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cpr_codigo")
  private Integer codigo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usu_codigo", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "for_codigo", nullable = false)
  private Fornecedor fornecedor;

  @Column(name = "cpr_emissao", nullable = false)
  private LocalDate emissao = LocalDate.now();

  @Column(name = "cpr_valor", precision = 18, scale = 2)
  private BigDecimal valor = BigDecimal.ZERO;

  @Column(name = "cpr_desconto", precision = 18, scale = 2)
  private BigDecimal desconto = BigDecimal.ZERO;

  @Column(name = "cpr_total", precision = 18, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;

  @Column(name = "cpr_dtentrada")
  private LocalDate dataEntrada;

  @Column(name = "cpr_obs", columnDefinition = "TEXT")
  private String obs;

  @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("codigo")
  private List<CompraProduto> itens = new ArrayList<>();

  public int getCodigo() {
    return codigo == null ? 0 : codigo;
  }

  public void setCodigo(int v) {
    codigo = v == 0 ? null : v;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario v) {
    usuario = v;
  }

  public Fornecedor getFornecedor() {
    return fornecedor;
  }

  public void setFornecedor(Fornecedor v) {
    fornecedor = v;
  }

  public LocalDate getEmissao() {
    return emissao;
  }

  public void setEmissao(LocalDate v) {
    emissao = v;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal v) {
    valor = v;
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

  public LocalDate getDataEntrada() {
    return dataEntrada;
  }

  public void setDataEntrada(LocalDate v) {
    dataEntrada = v;
  }

  public String getObs() {
    return obs;
  }

  public void setObs(String v) {
    obs = v;
  }

  public List<CompraProduto> getItens() {
    return itens;
  }

  public void setItens(List<CompraProduto> v) {
    itens = v == null ? new ArrayList<>() : v;
    itens.forEach(i -> i.setCompra(this));
  }
}
