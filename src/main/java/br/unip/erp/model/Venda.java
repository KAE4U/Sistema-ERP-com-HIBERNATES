package br.unip.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "venda")
public class Venda {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vda_codigo")
  private Integer codigo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usu_codigo", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cli_codigo", nullable = false)
  private Cliente cliente;

  @Column(name = "vda_data", nullable = false)
  private LocalDate data = LocalDate.now();

  @Column(name = "vda_valor", precision = 18, scale = 2)
  private BigDecimal valor = BigDecimal.ZERO;

  @Column(name = "vda_desconto", precision = 18, scale = 2)
  private BigDecimal desconto = BigDecimal.ZERO;

  @Column(name = "vda_total", precision = 18, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;

  @Column(name = "vda_obs", columnDefinition = "TEXT")
  private String obs;

  @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("codigo")
  private List<VendaProduto> itens = new ArrayList<>();

  @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("codigo")
  private List<VendaPagamento> pagamentos = new ArrayList<>();

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

  public Cliente getCliente() {
    return cliente;
  }

  public void setCliente(Cliente v) {
    cliente = v;
  }

  public LocalDate getData() {
    return data;
  }

  public void setData(LocalDate v) {
    data = v;
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

  public String getObs() {
    return obs;
  }

  public void setObs(String v) {
    obs = v;
  }

  public List<VendaProduto> getItens() {
    return itens;
  }

  public void setItens(List<VendaProduto> v) {
    itens = v == null ? new ArrayList<>() : v;
    itens.forEach(i -> i.setVenda(this));
  }

  public List<VendaPagamento> getPagamentos() {
    return pagamentos;
  }

  public void setPagamentos(List<VendaPagamento> v) {
    pagamentos = v == null ? new ArrayList<>() : v;
    pagamentos.forEach(p -> p.setVenda(this));
  }
}
