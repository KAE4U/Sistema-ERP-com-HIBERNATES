package br.unip.erp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "usu_codigo")
  private Integer codigo;

  @Column(name = "usu_nome", length = 80)
  private String nome;

  @Column(name = "usu_login", nullable = false, unique = true, length = 20)
  private String login;

  @Column(name = "usu_senha", length = 100)
  private String senha;

  @Column(name = "usu_cadastro")
  private LocalDate cadastro = LocalDate.now();

  @Column(name = "usu_ativo", nullable = false, length = 1, columnDefinition = "CHAR(1)")
  private String ativo = "S";

  @Column(name = "usu_admin", nullable = false, length = 1, columnDefinition = "CHAR(1)")
  private String admin = "N";

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

  public String getLogin() {
    return login;
  }

  public void setLogin(String v) {
    login = v;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String v) {
    senha = v;
  }

  public LocalDate getCadastro() {
    return cadastro;
  }

  public void setCadastro(LocalDate v) {
    cadastro = v;
  }

  public String getAtivo() {
    return ativo;
  }

  public void setAtivo(String v) {
    ativo = v;
  }

  public String getAdmin() {
    return admin;
  }

  public void setAdmin(String v) {
    admin = v;
  }

  public boolean isAdmin() {
    return "S".equalsIgnoreCase(admin);
  }

  @Override
  public String toString() {
    return nome != null ? nome : login;
  }
}
