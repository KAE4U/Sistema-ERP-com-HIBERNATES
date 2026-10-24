package br.unip.erp.dao;

import br.unip.erp.model.Usuario;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.*;
import java.util.List;

public class UsuarioDAO extends JpaGenericDAO<Usuario, Integer> {
  public UsuarioDAO() {
    super(Usuario.class);
  }

  @Override
  public List<Usuario> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery("select u from Usuario u order by u.nome", Usuario.class)
          .getResultList();
    }
  }

  public Usuario buscarPorLogin(EntityManager em, String login) {
    return em.createQuery(
            "select u from Usuario u where u.login=:login and u.ativo='S'", Usuario.class)
        .setParameter("login", login)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public Usuario buscarPorLogin(String login) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return buscarPorLogin(em, login);
    }
  }
}
