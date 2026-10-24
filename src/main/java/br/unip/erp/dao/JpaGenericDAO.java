package br.unip.erp.dao;

import br.unip.erp.util.JpaUtil;
import jakarta.persistence.*;
import java.util.List;
import java.util.function.Function;

/** Implementação reutilizável de CRUD com EntityManager e transações locais. */
public abstract class JpaGenericDAO<T, ID> implements GenericDAO<T, ID> {
  protected final Class<T> tipo;

  protected JpaGenericDAO(Class<T> tipo) {
    this.tipo = tipo;
  }

  @Override
  public ID inserir(T e) {
    return transacionar(
        em -> {
          em.persist(e);
          em.flush();
          return id(e);
        });
  }

  @Override
  public T atualizar(T e) {
    return transacionar(em -> em.merge(e));
  }

  @Override
  public void excluir(ID id) {
    transacionar(
        em -> {
          T e = em.find(tipo, id);
          if (e != null) em.remove(e);
          return null;
        });
  }

  @Override
  public T buscarPorCodigo(ID id) {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.find(tipo, id);
    } catch (RuntimeException e) {
      throw erro("buscar", e);
    }
  }

  @Override
  public List<T> listar() {
    try (EntityManager em = JpaUtil.getEntityManager()) {
      return em.createQuery("select e from " + tipo.getSimpleName() + " e", tipo).getResultList();
    } catch (RuntimeException e) {
      throw erro("listar", e);
    }
  }

  @SuppressWarnings("unchecked")
  protected ID id(T e) {
    return (ID) JpaUtil.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(e);
  }

  protected <R> R transacionar(Function<EntityManager, R> op) {
    EntityManager em = JpaUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      R r = op.apply(em);
      tx.commit();
      return r;
    } catch (RuntimeException e) {
      if (tx.isActive()) tx.rollback();
      throw erro("persistir", e);
    } finally {
      em.close();
    }
  }

  protected DAOException erro(String acao, RuntimeException e) {
    return e instanceof DAOException d
        ? d
        : new DAOException(
            "Erro ao " + acao + " " + tipo.getSimpleName() + ": " + e.getMessage(), e);
  }
}
