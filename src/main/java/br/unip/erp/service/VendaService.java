package br.unip.erp.service;

import br.unip.erp.dao.*;
import br.unip.erp.model.*;
import br.unip.erp.util.JpaUtil;
import jakarta.persistence.*;
import java.math.*;
import java.util.*;

public class VendaService {
  private static final int ESCALA = 2;
  private static final RoundingMode ARRED = RoundingMode.HALF_UP;

  public int registrar(Venda v) {
    validar(v);
    EntityManager em = JpaUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      Usuario u = referencia(em, Usuario.class, v.getUsuario().getCodigo(), "Usuário");
      Cliente c = referencia(em, Cliente.class, v.getCliente().getCodigo(), "Cliente");
      ativo(u.getAtivo(), "Usuário");
      ativo(c.getPessoa().getAtivo(), "Cliente");
      v.setUsuario(u);
      v.setCliente(c);
      Map<Integer, BigDecimal> pedidos = new TreeMap<>();
      for (VendaProduto i : v.getItens())
        pedidos.merge(i.getProduto().getCodigo(), i.getQtde(), BigDecimal::add);
      Map<Integer, Produto> produtos = new HashMap<>();
      for (var e : pedidos.entrySet()) {
        Produto p = em.find(Produto.class, e.getKey(), LockModeType.PESSIMISTIC_WRITE);
        if (p == null) throw new IllegalArgumentException("Produto não encontrado: " + e.getKey());
        ativo(p.getAtivo(), "Produto " + p.getNome());
        if (nz(p.getEstoque()).compareTo(e.getValue()) < 0)
          throw new IllegalArgumentException(
              "Estoque insuficiente para " + p.getNome() + ". Disponível: " + p.getEstoque());
        produtos.put(e.getKey(), p);
      }
      for (VendaProduto i : v.getItens()) {
        Produto p = produtos.get(i.getProduto().getCodigo());
        p.setEstoque(nz(p.getEstoque()).subtract(i.getQtde()));
        i.setProduto(p);
        i.setVenda(v);
      }
      for (VendaPagamento p : v.getPagamentos()) {
        FormaPagamento f =
            referencia(
                em, FormaPagamento.class, p.getFormaPagamento().getCodigo(), "Forma de pagamento");
        ativo(f.getAtivo(), "Forma de pagamento");
        p.setFormaPagamento(f);
        p.setVenda(v);
      }
      em.persist(v);
      em.flush();
      tx.commit();
      return v.getCodigo();
    } catch (RuntimeException e) {
      if (tx.isActive()) tx.rollback();
      throw e instanceof IllegalArgumentException
          ? e
          : new DAOException("Erro ao registrar venda: " + e.getMessage(), e);
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
      Venda v = em.find(Venda.class, id);
      if (v == null) throw new IllegalArgumentException("Venda não encontrada.");
      List<VendaProduto> itens = new ArrayList<>(v.getItens());
      itens.sort(Comparator.comparingInt(i -> i.getProduto().getCodigo()));
      for (VendaProduto i : itens) {
        Produto p =
            em.find(Produto.class, i.getProduto().getCodigo(), LockModeType.PESSIMISTIC_WRITE);
        p.setEstoque(nz(p.getEstoque()).add(i.getQtde()));
      }
      em.remove(v);
      tx.commit();
    } catch (RuntimeException e) {
      if (tx.isActive()) tx.rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  public void recalcular(Venda v) {
    BigDecimal valor = BigDecimal.ZERO;
    for (VendaProduto i : v.getItens()) {
      BigDecimal bruto = nz(i.getPreco()).multiply(nz(i.getQtde()));
      BigDecimal total = dinheiro(bruto.subtract(nz(i.getDesconto())));
      i.setPreco(dinheiro(i.getPreco()));
      i.setDesconto(dinheiro(i.getDesconto()));
      i.setTotal(total);
      valor = valor.add(total);
    }
    v.setValor(dinheiro(valor));
    v.setDesconto(dinheiro(v.getDesconto()));
    v.setTotal(dinheiro(v.getValor().subtract(v.getDesconto())));
  }

  private void validar(Venda v) {
    if (v.getCliente() == null || v.getCliente().getCodigo() == 0)
      throw new IllegalArgumentException("Selecione um cliente para a venda.");
    if (v.getUsuario() == null || v.getUsuario().getCodigo() == 0)
      throw new IllegalArgumentException("Usuário da venda não identificado.");
    if (v.getItens() == null || v.getItens().isEmpty())
      throw new IllegalArgumentException("Adicione ao menos um produto à venda.");
    for (VendaProduto i : v.getItens()) {
      if (i.getProduto() == null || i.getProduto().getCodigo() == 0)
        throw new IllegalArgumentException("Produto inválido.");
      positivo(i.getQtde(), "Quantidade");
      positivo(i.getPreco(), "Preço");
      naoNegativo(i.getDesconto(), "Desconto do item");
      if (nz(i.getDesconto()).compareTo(nz(i.getPreco()).multiply(i.getQtde())) > 0)
        throw new IllegalArgumentException("Desconto do item não pode superar seu valor bruto.");
    }
    naoNegativo(v.getDesconto(), "Desconto");
    recalcular(v);
    if (v.getTotal().compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("O total da venda deve ser positivo.");
    if (v.getPagamentos() == null || v.getPagamentos().isEmpty())
      throw new IllegalArgumentException("Informe ao menos um pagamento.");
    BigDecimal soma = BigDecimal.ZERO;
    for (VendaPagamento p : v.getPagamentos()) {
      if (p.getFormaPagamento() == null || p.getFormaPagamento().getCodigo() == 0)
        throw new IllegalArgumentException("Forma de pagamento inválida.");
      positivo(p.getValor(), "Valor do pagamento");
      p.setValor(dinheiro(p.getValor()));
      soma = soma.add(p.getValor());
    }
    if (dinheiro(soma).compareTo(v.getTotal()) != 0)
      throw new IllegalArgumentException("A soma dos pagamentos deve ser igual ao total da venda.");
  }

  private <T> T referencia(EntityManager em, Class<T> c, int id, String nome) {
    T r = em.find(c, id);
    if (r == null) throw new IllegalArgumentException(nome + " não encontrado.");
    return r;
  }

  private void ativo(String flag, String nome) {
    if (!"S".equalsIgnoreCase(flag)) throw new IllegalArgumentException(nome + " está inativo.");
  }

  private BigDecimal dinheiro(BigDecimal v) {
    return nz(v).setScale(ESCALA, ARRED);
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
