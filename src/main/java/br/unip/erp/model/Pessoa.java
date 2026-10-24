package br.unip.erp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pessoa")
public class Pessoa {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pes_codigo")
  private Integer codigo;

  @Column(name = "pes_nome", nullable = false, length = 80)
  private String nome;

  @Column(name = "pes_fantasia", length = 80)
  private String fantasia;

  @Column(name = "pes_fisica", nullable = false, length = 1, columnDefinition = "CHAR(1)")
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
  private String fisica = "F";

  @Column(name = "pes_cpfcnpj", length = 20)
  private String cpfCnpj;

  @Column(name = "pes_rgie", length = 20)
  private String rgIe;

  @Column(name = "pes_cadastro")
  private LocalDate cadastro = LocalDate.now();

  @Column(name = "pes_endereco", length = 120)
  private String endereco;

  @Column(name = "pes_numero", length = 10)
  private String numero;

  @Column(name = "pes_complemento", length = 30)
  private String complemento;

  @Column(name = "pes_bairro", length = 50)
  private String bairro;

  @Column(name = "pes_cidade", length = 80)
  private String cidade;

  @Column(name = "pes_uf", length = 2, columnDefinition = "CHAR(2)")
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
  private String uf;

  @Column(name = "pes_cep", length = 9)
  private String cep;

  @Column(name = "pes_fone1", length = 16)
  private String fone1;

  @Column(name = "pes_fone2", length = 16)
  private String fone2;

  @Column(name = "pes_celular", length = 16)
  private String celular;

  @Column(name = "pes_site", length = 200)
  private String site;

  @Column(name = "pes_email", length = 200)
  private String email;

  @Column(name = "pes_ativo", nullable = false, length = 1, columnDefinition = "CHAR(1)")
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

  public String getFantasia() {
    return fantasia;
  }

  public void setFantasia(String v) {
    fantasia = v;
  }

  public String getFisica() {
    return fisica;
  }

  public void setFisica(String v) {
    fisica = v;
  }

  public String getCpfCnpj() {
    return cpfCnpj;
  }

  public void setCpfCnpj(String v) {
    cpfCnpj = v;
  }

  public String getRgIe() {
    return rgIe;
  }

  public void setRgIe(String v) {
    rgIe = v;
  }

  public LocalDate getCadastro() {
    return cadastro;
  }

  public void setCadastro(LocalDate v) {
    cadastro = v;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String v) {
    endereco = v;
  }

  public String getNumero() {
    return numero;
  }

  public void setNumero(String v) {
    numero = v;
  }

  public String getComplemento() {
    return complemento;
  }

  public void setComplemento(String v) {
    complemento = v;
  }

  public String getBairro() {
    return bairro;
  }

  public void setBairro(String v) {
    bairro = v;
  }

  public String getCidade() {
    return cidade;
  }

  public void setCidade(String v) {
    cidade = v;
  }

  public String getUf() {
    return uf;
  }

  public void setUf(String v) {
    uf = v;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String v) {
    cep = v;
  }

  public String getFone1() {
    return fone1;
  }

  public void setFone1(String v) {
    fone1 = v;
  }

  public String getFone2() {
    return fone2;
  }

  public void setFone2(String v) {
    fone2 = v;
  }

  public String getCelular() {
    return celular;
  }

  public void setCelular(String v) {
    celular = v;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String v) {
    site = v;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String v) {
    email = v;
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
