package br.unip.erp.view;

import br.unip.erp.util.*;
import javax.swing.*;

/** Janela MDI principal com dashboard automático e todos os módulos do ERP. */
public class MenuView extends JFrame {
  private final JDesktopPane desktop = new JDesktopPane();
  private DashboardView dashboard;

  public MenuView() {
    if (Sessao.getUsuarioLogado() == null) throw new SecurityException("Sessão não autenticada.");
    setTitle("EMPRESA X - ERP Hibernate");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1180, 760);
    setLocationRelativeTo(null);
    desktop.setBackground(Tema.FUNDO);
    setContentPane(desktop);
    setJMenuBar(menu());
    SwingUtilities.invokeLater(this::abrirDashboard);
  }

  private JMenuBar menu() {
    JMenuBar b = new JMenuBar();
    JMenu dash = new JMenu("Dashboard");
    JMenuItem abrirDash = new JMenuItem("Abrir"), atualizar = new JMenuItem("Atualizar");
    abrirDash.addActionListener(e -> abrirDashboard());
    atualizar.addActionListener(
        e -> {
          if (dashboard == null || dashboard.isClosed()) {
            abrirDashboard(); // o construtor já inicia a primeira carga
          } else {
            dashboard.toFront();
            dashboard.atualizar();
          }
        });
    dash.add(abrirDash);
    dash.add(atualizar);
    JMenu cad = new JMenu("Cadastros");
    item(cad, "Cliente", ClienteView::new);
    item(cad, "Fornecedor", FornecedorView::new);
    item(cad, "Forma de Pagamento", FormaPagamentoView::new);
    JMenuItem usuario = item(cad, "Usuário", UsuarioView::new);
    usuario.setEnabled(Sessao.getUsuarioLogado().isAdmin());
    item(cad, "Produto", ProdutoView::new);
    JMenu mov = new JMenu("Movimentos");
    item(mov, "Venda", VendaView::new);
    item(mov, "Compra", CompraView::new);
    mov.addSeparator();
    item(mov, "Histórico de Vendas", HistoricoVendaView::new);
    item(mov, "Histórico de Compras", HistoricoCompraView::new);
    JMenu rel = new JMenu("Relatórios");
    item(rel, "Análises", RelatorioView::new);
    JMenuItem todos = new JMenuItem("Gerar todos os PDFs");
    todos.addActionListener(e -> ReportActions.gerarTodos(this));
    rel.add(todos);
    for (var t : br.unip.erp.service.ReportService.Tipo.values()) {
      JMenuItem i = new JMenuItem("PDF - " + t.name().replace('_', ' '));
      i.addActionListener(e -> ReportActions.gerar(this, t));
      rel.add(i);
    }
    JMenu ex = new JMenu("Exibir");
    JMenuItem esc = new JMenuItem("Tema escuro"), cla = new JMenuItem("Tema claro");
    esc.addActionListener(e -> Tema.aplicarEscuro(this));
    cla.addActionListener(e -> Tema.aplicarClaro(this));
    ex.add(esc);
    ex.add(cla);
    JMenu sair = new JMenu("Sair");
    JMenuItem logout = new JMenuItem("Trocar usuário"), fim = new JMenuItem("Encerrar");
    logout.addActionListener(
        e -> {
          Sessao.encerrar();
          dispose();
          new LoginView().setVisible(true);
        });
    fim.addActionListener(
        e -> {
          if (JOptionPane.showConfirmDialog(
                  this, "Deseja encerrar?", "Sair", JOptionPane.YES_NO_OPTION)
              == JOptionPane.YES_OPTION) {
            JpaUtil.close();
            System.exit(0);
          }
        });
    sair.add(logout);
    sair.add(fim);
    b.add(dash);
    b.add(cad);
    b.add(mov);
    b.add(rel);
    b.add(ex);
    b.add(sair);
    return b;
  }

  private JMenuItem item(
      JMenu m, String nome, java.util.function.Supplier<JInternalFrame> fabrica) {
    JMenuItem i = new JMenuItem(nome);
    i.addActionListener(e -> abrir(fabrica.get()));
    m.add(i);
    return i;
  }

  private void abrirDashboard() {
    if (dashboard == null || dashboard.isClosed()) {
      dashboard = new DashboardView();
      abrir(dashboard);
    } else dashboard.toFront();
  }

  private void abrir(JInternalFrame f) {
    for (JInternalFrame a : desktop.getAllFrames())
      if (a.getClass().equals(f.getClass())) {
        a.toFront();
        return;
      }
    desktop.add(f);
    f.setVisible(true);
    f.toFront();
    try {
      f.setMaximum(f instanceof DashboardView);
    } catch (Exception ignored) {
    }
  }
}
