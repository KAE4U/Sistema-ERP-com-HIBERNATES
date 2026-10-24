package br.unip.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedor")
public class Fornecedor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "for_codigo")
  private Integer codigo;

  @OneToOne(cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "pes_codigo", nullable = false, unique = true)
  private Pessoa pessoa = new Pessoa();

  @Column(name = "for_contato", length = 80)
  private String contato;

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

  public String getContato() {
    return contato;
  }

  public void setContato(String v) {
    contato = v;
  }

  @Override
  public String toString() {
    return pessoa != null ? pessoa.getNome() : "Fornecedor " + getCodigo();
  }
}
