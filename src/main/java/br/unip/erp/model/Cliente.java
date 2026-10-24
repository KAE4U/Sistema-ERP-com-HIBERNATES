package br.unip.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cliente")
public class Cliente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cli_codigo")
  private Integer codigo;

  @OneToOne(cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "pes_codigo", nullable = false, unique = true)
  private Pessoa pessoa = new Pessoa();

  @Column(name = "cli_limitecred", precision = 18, scale = 2)
  private BigDecimal limiteCredito = BigDecimal.ZERO;

  public int getCodigo() {
    return codigo == null ? 0 : codigo;
  }

  public void setCodigo(int v) {
    codigo = v == 0 ? null : v;
  }

  public Pessoa getPessoa() {
    return pessoa;
  }

  public void setPessoa(Pessoa v) {
    pessoa = v;
  }

  public BigDecimal getLimiteCredito() {
    return limiteCredito;
  }

  public void setLimiteCredito(BigDecimal v) {
    limiteCredito = v;
  }

  @Override
  public String toString() {
    return pessoa != null ? pessoa.getNome() : "Cliente " + getCodigo();
  }
}
