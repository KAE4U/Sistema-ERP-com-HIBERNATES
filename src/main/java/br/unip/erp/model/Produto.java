package br.unip.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "produto")
public class Produto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pro_codigo")
  private Integer codigo;

  @Column(name = "pro_nome", nullable = false, length = 80)
  private String nome;

  @Column(name = "pro_tipo", length = 1, columnDefinition = "CHAR(1)")
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
  private String tipo;

  @Column(name = "pro_estoque", precision = 14, scale = 4)
  private BigDecimal estoque = BigDecimal.ZERO;

  @Column(name = "pro_unidade", length = 5)
  private String unidade;

  @Column(name = "pro_preco", precision = 18, scale = 2)
  private BigDecimal preco = BigDecimal.ZERO;

  @Column(name = "pro_custo", precision = 18, scale = 2)
  private BigDecimal custo = BigDecimal.ZERO;

  @Column(name = "pro_atacado", precision = 18, scale = 2)
  private BigDecimal atacado = BigDecimal.ZERO;

  @Column(name = "pro_min", precision = 14, scale = 4)
  private BigDecimal minimo = BigDecimal.ZERO;

  @Column(name = "pro_max", precision = 14, scale = 4)
  private BigDecimal maximo = BigDecimal.ZERO;

  @Column(name = "pro_embalagem", precision = 9, scale = 0)
  private BigDecimal embalagem = BigDecimal.ZERO;

  @Column(name = "pro_peso", precision = 14, scale = 4)
  private BigDecimal peso = BigDecimal.ZERO;

  @Column(name = "pro_cadastro")
  private LocalDate cadastro = LocalDate.now();

  @Column(name = "pro_obs", columnDefinition = "TEXT")
  private String obs;

  @Column(name = "pro_ativo", nullable = false, length = 1, columnDefinition = "CHAR(1)")
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
  private String ativo = "S";

  public int getCodigo() {
    return codigo == null ? 0 : codigo;
  }

  public void setCodigo(int v) {
    codigo = v == 0 ? null : v;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String v) {
    nome = v;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String v) {
    tipo = v;
  }

  public BigDecimal getEstoque() {
    return estoque;
  }

  public void setEstoque(BigDecimal v) {
    estoque = v;
  }

  public String getUnidade() {
    return unidade;
  }

  public void setUnidade(String v) {
    unidade = v;
  }

  public BigDecimal getPreco() {
    return preco;
  }

  public void setPreco(BigDecimal v) {
    preco = v;
  }

  public BigDecimal getCusto() {
    return custo;
  }

  public void setCusto(BigDecimal v) {
    custo = v;
  }

  public BigDecimal getAtacado() {
    return atacado;
  }

  public void setAtacado(BigDecimal v) {
    atacado = v;
  }

  public BigDecimal getMinimo() {
    return minimo;
  }

  public void setMinimo(BigDecimal v) {
    minimo = v;
  }

  public BigDecimal getMaximo() {
    return maximo;
  }

  public void setMaximo(BigDecimal v) {
    maximo = v;
  }

  public BigDecimal getEmbalagem() {
    return embalagem;
  }

  public void setEmbalagem(BigDecimal v) {
    embalagem = v;
  }

  public BigDecimal getPeso() {
    return peso;
  }

  public void setPeso(BigDecimal v) {
    peso = v;
  }

  public LocalDate getCadastro() {
    return cadastro;
  }

  public void setCadastro(LocalDate v) {
    cadastro = v;
  }

  public String getObs() {
    return obs;
  }

  public void setObs(String v) {
    obs = v;
  }

  public String getAtivo() {
    return ativo;
  }

  public void setAtivo(String v) {
    ativo = v;
  }

  @Override
  public String toString() {
    return nome;
  }
}
