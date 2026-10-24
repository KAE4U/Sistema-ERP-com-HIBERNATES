package br.unip.erp.service;

import br.unip.erp.dao.*;
import br.unip.erp.model.*;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.*;
import java.math.*;
import java.util.*;

public class CompraService {
  private static final RoundingMode ARRED = RoundingMode.HALF_UP;

  public int registrar(Compra c) {
    validar(c);
    EntityManager em = JpaUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      Usuario u = ref(em, Usuario.class, c.getUsuario().getCodigo(), "Usuário");
      Fornecedor f = ref(em, Fornecedor.class, c.getFornecedor().getCodigo(), "Fornecedor");
      ativo(u.getAtivo(), "Usuário");
      ativo(f.getPessoa().getAtivo(), "Fornecedor");
      c.setUsuario(u);
      c.setFornecedor(f);
      Map<Integer, Produto> produtos = new HashMap<>();
      for (Integer id :
          c.getItens().stream().map(i -> i.getProduto().getCodigo()).distinct().sorted().toList()) {
        Produto p = em.find(Produto.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (p == null) throw new IllegalArgumentException("Produto não encontrado.");
        ativo(p.getAtivo(), "Produto " + p.getNome());
        produtos.put(id, p);
      }
      for (CompraProduto i : c.getItens()) {
        Produto p = produtos.get(i.getProduto().getCodigo());
        p.setEstoque(nz(p.getEstoque()).add(i.getQtde()));
        i.setProduto(p);
        i.setCompra(c);
      }
      em.persist(c);
      em.flush();
      tx.commit();
      return c.getCodigo();
    } catch (RuntimeException e) {
      if (tx.isActive()) tx.rollback();
      throw e instanceof IllegalArgumentException
          ? e
          : new DAOException("Erro ao registrar compra: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void excluir(int id) {
    AutorizacaoService.exigirAdmin();
    EntityManager em = JpaUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      Compra c = em.find(Compra.class, id);
      if (c == null) throw new IllegalArgumentException("Compra não encontrada.");
      List<CompraProduto> itens = new ArrayList<>(c.getItens());
      itens.sort(Comparator.comparingInt(i -> i.getProduto().getCodigo()));
      for (CompraProduto i : itens) {
        Produto p =
            em.find(Produto.class, i.getProduto().getCodigo(), LockModeType.PESSIMISTIC_WRITE);
        BigDecimal novo = nz(p.getEstoque()).subtract(i.getQtde());
        if (novo.signum() < 0)
          throw new IllegalStateException(
              "Não é possível excluir: o estoque de " + p.getNome() + " ficaria negativo.");
        p.setEstoque(novo);
      }
      em.remove(c);
      tx.commit();
    } catch (RuntimeException e) {
      if (tx.isActive()) tx.rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  public void recalcular(Compra c) {
    BigDecimal valor = BigDecimal.ZERO;
    for (CompraProduto i : c.getItens()) {
      BigDecimal bruto = nz(i.getPreco()).multiply(nz(i.getQtde()));
      i.setPreco(dinheiro(i.getPreco()));
      i.setDesconto(dinheiro(i.getDesconto()));
      i.setTotal(dinheiro(bruto.subtract(i.getDesconto())));
      valor = valor.add(i.getTotal());
    }
    c.setValor(dinheiro(valor));
    c.setDesconto(dinheiro(c.getDesconto()));
    c.setTotal(dinheiro(c.getValor().subtract(c.getDesconto())));
  }

  private void validar(Compra c) {
    if (c.getFornecedor() == null || c.getFornecedor().getCodigo() == 0)
      throw new IllegalArgumentException("Selecione um fornecedor para a compra.");
    if (c.getUsuario() == null || c.getUsuario().getCodigo() == 0)
      throw new IllegalArgumentException("Usuário da compra não identificado.");
    if (c.getItens() == null || c.getItens().isEmpty())
      throw new IllegalArgumentException("Adicione ao menos um produto à compra.");
    for (CompraProduto i : c.getItens()) {
      if (i.getProduto() == null || i.getProduto().getCodigo() == 0)
        throw new IllegalArgumentException("Produto inválido.");
      positivo(i.getQtde(), "Quantidade");
      positivo(i.getPreco(), "Preço");
      naoNegativo(i.getDesconto(), "Desconto do item");
      if (nz(i.getDesconto()).compareTo(nz(i.getPreco()).multiply(i.getQtde())) > 0)
        throw new IllegalArgumentException("Desconto do item não pode superar seu valor bruto.");
    }
    naoNegativo(c.getDesconto(), "Desconto");
    recalcular(c);
    if (c.getTotal().signum() <= 0)
      throw new IllegalArgumentException("O total da compra deve ser positivo.");
  }

  private <T> T ref(EntityManager em, Class<T> t, int id, String n) {
    T r = em.find(t, id);
    if (r == null) throw new IllegalArgumentException(n + " não encontrado.");
    return r;
  }

  private void ativo(String f, String n) {
    if (!"S".equalsIgnoreCase(f)) throw new IllegalArgumentException(n + " está inativo.");
  }

  private BigDecimal dinheiro(BigDecimal v) {
    return nz(v).setScale(2, ARRED);
  }

  private BigDecimal nz(BigDecimal v) {
    return v == null ? BigDecimal.ZERO : v;
  }

  private void positivo(BigDecimal v, String n) {
    if (v == null || v.signum() <= 0) throw new IllegalArgumentException(n + " deve ser positivo.");
  }

  private void naoNegativo(BigDecimal v, String n) {
    if (v != null && v.signum() < 0)
      throw new IllegalArgumentException(n + " não pode ser negativo.");
  }
}
