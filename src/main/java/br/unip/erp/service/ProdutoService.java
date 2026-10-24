package br.unip.erp.service;

import br.unip.erp.dao.DAOException;
import br.unip.erp.model.Produto;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Atualiza campos cadastrais sem sobrescrever estoque movimentado ou campos não expostos na tela.
 */
public class ProdutoService {
  public int salvar(Produto entrada) {
    entrada.setTipo(normalizarTipo(entrada.getTipo()));
    validar(entrada);
    if (entrada.getCodigo() == 0) {
      EntityManager em = JpaUtil.getEntityManager();
      EntityTransaction tx = em.getTransaction();
      try {
        tx.begin();
        em.persist(entrada);
        em.flush();
        tx.commit();
        return entrada.getCodigo();
      } catch (RuntimeException e) {
        if (tx.isActive()) tx.rollback();
        throw new DAOException("Erro ao inserir produto: " + e.getMessage(), e);
      } finally {
        em.close();
      }
    }
    EntityManager em = JpaUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      Produto p = em.find(Produto.class, entrada.getCodigo(), LockModeType.PESSIMISTIC_WRITE);
      if (p == null) throw new IllegalArgumentException("Produto não encontrado.");
      p.setNome(entrada.getNome());
      p.setTipo(entrada.getTipo());
      p.setUnidade(entrada.getUnidade());
      p.setPreco(entrada.getPreco());
      p.setCusto(entrada.getCusto());
      p.setMinimo(entrada.getMinimo());
      p.setAtivo(entrada.getAtivo());
      tx.commit();
      return p.getCodigo();
    } catch (RuntimeException e) {
      if (tx.isActive()) tx.rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  private String normalizarTipo(String tipo) {
    if (tipo == null || tipo.isBlank()) return null;
    String normalizado = tipo.trim().toUpperCase(java.util.Locale.ROOT);
    if (normalizado.length() != 1)
      throw new IllegalArgumentException("O tipo do produto deve possuir exatamente 1 caractere.");
    return normalizado;
  }

  private void validar(Produto p) {
    if (p.getNome() == null || p.getNome().isBlank())
      throw new IllegalArgumentException("O nome do produto é obrigatório.");
    naoNegativo(p.getEstoque(), "Estoque");
    naoNegativo(p.getPreco(), "Preço");
    naoNegativo(p.getCusto(), "Custo");
    naoNegativo(p.getMinimo(), "Estoque mínimo");
  }

  private void naoNegativo(BigDecimal v, String n) {
    if (v != null && v.signum() < 0)
      throw new IllegalArgumentException(n + " não pode ser negativo.");
  }
}
