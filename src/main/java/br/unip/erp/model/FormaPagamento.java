package br.unip.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "formapagto")
public class FormaPagamento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "fpg_codigo")
  private Integer codigo;

  @Column(name = "fpg_nome", nullable = false, length = 80)
  private String nome;

  @Column(name = "fpg_ativo", nullable = false, length = 1, columnDefinition = "CHAR(1)")
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
