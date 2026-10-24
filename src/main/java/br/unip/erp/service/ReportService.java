package br.unip.erp.service;

import br.unip.erp.dao.*;
import br.unip.erp.model.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;

/** Gera PDFs tabulares sem expor credenciais de usuários. */
public class ReportService {
  public enum Tipo {
    USUARIOS("usuarios"),
    CLIENTES("clientes"),
    FORNECEDORES("fornecedores"),
    PRODUTOS("produtos_estoque"),
    FORMAS_PAGAMENTO("formas_pagamento"),
    VENDAS("vendas"),
    COMPRAS("compras");
    final String arq;

    Tipo(String a) {
      arq = a;
    }
  }

  public Path gerar(Tipo tipo) {
    try {
      Files.createDirectories(Path.of("relatorios"));
      Path out =
          Path.of(
              "relatorios",
              tipo.arq
                  + "_"
                  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"))
                  + ".pdf");
      List<String[]> rows = dados(tipo);
      escrever(out, titulo(tipo), cabecalho(tipo), rows);
      return out.toAbsolutePath();
    } catch (IOException | RuntimeException e) {
      throw new DAOException("Erro ao gerar PDF de " + tipo.arq + ": " + e.getMessage(), e);
    }
  }

  public List<Path> gerarTodos() {
    List<Path> r = new ArrayList<>();
    for (Tipo t : Tipo.values()) r.add(gerar(t));
    return r;
  }

  private List<String[]> dados(Tipo t) {
    List<String[]> r = new ArrayList<>();
    switch (t) {
      case USUARIOS ->
          new UsuarioDAO()
              .listar()
              .forEach(
                  u ->
                      r.add(
                          new String[] {
                            "" + u.getCodigo(),
                            safe(u.getNome()),
                            u.getLogin(),
                            u.getAtivo(),
                            u.getAdmin()
                          }));
      case CLIENTES ->
          new ClienteDAO()
              .listar()
              .forEach(
                  c ->
                      r.add(
                          new String[] {
                            "" + c.getCodigo(),
                            safe(c.getPessoa().getNome()),
                            safe(c.getPessoa().getCpfCnpj()),
                            moeda(c.getLimiteCredito()),
                            c.getPessoa().getAtivo()
                          }));
      case FORNECEDORES ->
          new FornecedorDAO()
              .listar()
              .forEach(
                  f ->
                      r.add(
                          new String[] {
                            "" + f.getCodigo(),
                            safe(f.getPessoa().getNome()),
                            safe(f.getPessoa().getCpfCnpj()),
                            safe(f.getContato()),
                            f.getPessoa().getAtivo()
                          }));
      case PRODUTOS ->
          new ProdutoDAO()
              .listar()
              .forEach(
                  p ->
                      r.add(
                          new String[] {
                            "" + p.getCodigo(),
                            safe(p.getNome()),
                            safe(p.getTipo()),
                            num(p.getEstoque()),
                            num(p.getMinimo()),
                            moeda(p.getPreco())
                          }));
      case FORMAS_PAGAMENTO ->
          new FormaPagamentoDAO()
              .listar()
              .forEach(f -> r.add(new String[] {"" + f.getCodigo(), f.getNome(), f.getAtivo()}));
      case VENDAS -> {
        VendaDAO d = new VendaDAO();
        for (Venda resumo : d.listar()) {
          Venda v = d.buscarPorCodigo(resumo.getCodigo());
          r.add(
              new String[] {
                "VENDA " + v.getCodigo(),
                String.valueOf(v.getData()),
                v.getCliente().toString(),
                moeda(v.getTotal())
              });
          for (VendaProduto i : v.getItens())
            r.add(
                new String[] {
                  "  Item", i.getProduto().getNome(), num(i.getQtde()), moeda(i.getTotal())
                });
          for (VendaPagamento p : v.getPagamentos())
            r.add(
                new String[] {
                  "  Pagamento", p.getFormaPagamento().getNome(), "", moeda(p.getValor())
                });
        }
      }
      case COMPRAS -> {
        CompraDAO d = new CompraDAO();
        for (Compra resumo : d.listar()) {
          Compra c = d.buscarPorCodigo(resumo.getCodigo());
          r.add(
              new String[] {
                "COMPRA " + c.getCodigo(),
                String.valueOf(c.getEmissao()),
                c.getFornecedor().toString(),
                moeda(c.getTotal())
              });
          for (CompraProduto i : c.getItens())
            r.add(
                new String[] {
                  "  Item", i.getProduto().getNome(), num(i.getQtde()), moeda(i.getTotal())
                });
        }
      }
    }
    return r;
  }

  private void escrever(Path path, String titulo, String[] headers, List<String[]> rows)
      throws IOException {
    try (PDDocument doc = new PDDocument()) {
      PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA),
          bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
      int porPagina = 34, total = Math.max(1, (rows.size() + porPagina - 1) / porPagina);
      for (int pg = 0; pg < total; pg++) {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
          texto(cs, bold, 16, 40, 800, titulo);
          texto(
              cs,
              normal,
              9,
              40,
              784,
              "Gerado em "
                  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                  + "  |  Página "
                  + (pg + 1)
                  + " de "
                  + total);
          texto(cs, bold, 9, 40, 762, String.join("  |  ", headers));
          float y = 744;
          for (int i = pg * porPagina; i < Math.min(rows.size(), (pg + 1) * porPagina); i++) {
            texto(cs, normal, 8, 40, y, limitar(String.join("  |  ", rows.get(i)), 125));
            y -= 20;
          }
        }
      }
      doc.save(path.toFile());
    }
  }

  private void texto(PDPageContentStream cs, PDFont f, float tam, float x, float y, String s)
      throws IOException {
    cs.beginText();
    cs.setFont(f, tam);
    cs.newLineAtOffset(x, y);
    cs.showText(pdfSafe(s).replace("\n", " "));
    cs.endText();
  }

  private String pdfSafe(String s) {
    String v = safe(s);
    java.nio.charset.CharsetEncoder e =
        java.nio.charset.Charset.forName("windows-1252").newEncoder();
    StringBuilder b = new StringBuilder();
    v.codePoints()
        .forEach(
            cp -> {
              String c = new String(Character.toChars(cp));
              b.append(e.canEncode(c) ? c : "?");
            });
    return b.toString();
  }

  private String limitar(String s, int n) {
    return s.length() <= n ? s : s.substring(0, n - 3) + "...";
  }

  private String titulo(Tipo t) {
    return "ERP - Relatório de " + t.arq.replace('_', ' ');
  }

  private String[] cabecalho(Tipo t) {
    return switch (t) {
      case USUARIOS -> new String[] {"Código", "Nome", "Login", "Ativo", "Admin"};
      case CLIENTES -> new String[] {"Código", "Nome", "CPF/CNPJ", "Limite", "Ativo"};
      case FORNECEDORES -> new String[] {"Código", "Nome", "CPF/CNPJ", "Contato", "Ativo"};
      case PRODUTOS -> new String[] {"Código", "Produto", "Tipo", "Estoque", "Mínimo", "Preço"};
      case FORMAS_PAGAMENTO -> new String[] {"Código", "Nome", "Ativo"};
      case VENDAS, COMPRAS ->
          new String[] {"Documento", "Data/Descrição", "Cliente/Fornecedor/Qtde", "Total"};
    };
  }

  private String safe(String s) {
    return s == null ? "" : s;
  }

  private String num(BigDecimal v) {
    return v == null ? "0" : v.stripTrailingZeros().toPlainString();
  }

  private String moeda(BigDecimal v) {
    return "R$ " + String.format(new Locale("pt", "BR"), "%,.2f", v == null ? BigDecimal.ZERO : v);
  }
}
