package br.unip.erp.view;

import br.unip.erp.controller.DashboardController;
import br.unip.erp.model.DashboardDTO;
import br.unip.erp.util.Tema;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/** Dashboard gerencial assíncrono com KPIs, tendência, mix e alerta de estoque. */
public class DashboardView extends JInternalFrame {
  private final DashboardController controller = new DashboardController();
  private final JLabel fat = valor(),
      compras = valor(),
      saldo = valor(),
      estoque = valor(),
      status = new JLabel("Carregando indicadores...");
  private final JPanel graficos = new JPanel(new GridLayout(1, 2, 12, 12));
  private SwingWorker<DashboardDTO, Void> carregamento;
  private long geracaoAtual;

  public DashboardView() {
    super("Dashboard gerencial", true, true, true, true);
    setSize(920, 570);
    setLayout(new BorderLayout(12, 12));
    JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
    cards.add(card("Faturamento", fat));
    cards.add(card("Custo de compras", compras));
    cards.add(card("Saldo", saldo));
    cards.add(card("Abaixo do mínimo", estoque));
    JButton atualizar = new JButton("Atualizar");
    Tema.botaoPrimario(atualizar);
    atualizar.addActionListener(e -> atualizar());
    JPanel topo = new JPanel(new BorderLayout());
    topo.add(cards);
    topo.add(atualizar, BorderLayout.EAST);
    add(topo, BorderLayout.NORTH);
    add(graficos, BorderLayout.CENTER);
    add(status, BorderLayout.SOUTH);
    atualizar();
  }

  private JPanel card(String titulo, JLabel valor) {
    JPanel p = new JPanel(new BorderLayout());
    p.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 72, 88)),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
    JLabel t = new JLabel(titulo);
    t.setForeground(Tema.TEXTO_SUAVE);
    p.add(t, BorderLayout.NORTH);
    p.add(valor);
    return p;
  }

  private JLabel valor() {
    JLabel l = new JLabel("—");
    l.setFont(new Font("SansSerif", Font.BOLD, 22));
    l.setForeground(Tema.TEXTO);
    return l;
  }

  public void atualizar() {
    long geracao = ++geracaoAtual;
    if (carregamento != null && !carregamento.isDone()) {
      carregamento.cancel(true);
    }

    status.setText("Atualizando...");
    carregamento =
        new SwingWorker<>() {
          @Override
          protected DashboardDTO doInBackground() {
            return controller.carregar();
          }

          @Override
          protected void done() {
            if (isCancelled() || geracao != geracaoAtual) {
              return;
            }
            try {
              render(get());
              status.setText("Atualizado agora.");
            } catch (Exception e) {
              status.setText("Falha ao carregar: " + e.getMessage());
              JOptionPane.showMessageDialog(
                  DashboardView.this, e.getMessage(), "Dashboard", JOptionPane.ERROR_MESSAGE);
            }
          }
        };
    carregamento.execute();
  }

  private void render(DashboardDTO d) {
    fat.setText(moeda(d.getFaturamento()));
    compras.setText(moeda(d.getCustoCompras()));
    saldo.setText(moeda(d.getSaldo()));
    saldo.setForeground(d.getSaldo().signum() < 0 ? Tema.PERIGO : Tema.SUCESSO);
    estoque.setText(String.valueOf(d.getEstoqueMinimo()));
    estoque.setForeground(d.getEstoqueMinimo() > 0 ? Tema.PERIGO : Tema.SUCESSO);
    graficos.removeAll();
    graficos.add(new ChartPanel(linhas(d)));
    graficos.add(new ChartPanel(donut(d)));
    graficos.revalidate();
    graficos.repaint();
  }

  private JFreeChart linhas(DashboardDTO d) {
    DefaultCategoryDataset ds = new DefaultCategoryDataset();
    DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/yy");
    for (var e : d.getVendasMensais().entrySet())
      ds.addValue(e.getValue(), "Vendas", e.getKey().format(f));
    for (var e : d.getComprasMensais().entrySet())
      ds.addValue(e.getValue(), "Compras", e.getKey().format(f));
    JFreeChart c = ChartFactory.createLineChart("Vendas x Compras — 12 meses", "Mês", "R$", ds);
    estilo(c);
    CategoryPlot p = c.getCategoryPlot();
    p.setRenderer(new LineAndShapeRenderer(true, true));
    return c;
  }

  private JFreeChart donut(DashboardDTO d) {
    DefaultPieDataset<String> ds = new DefaultPieDataset<>();
    d.getMixPagamentos().forEach(ds::setValue);
    JFreeChart c = ChartFactory.createRingChart("Mix de recebimentos", ds, true, true, false);
    estilo(c);
    RingPlot p = (RingPlot) c.getPlot();
    p.setSectionDepth(.35);
    p.setBackgroundPaint(Tema.SUPERFICIE);
    p.setOutlineVisible(false);
    p.setLabelBackgroundPaint(Tema.SUPERFICIE);
    p.setLabelPaint(Tema.TEXTO);
    return c;
  }

  private void estilo(JFreeChart c) {
    c.setBackgroundPaint(Tema.SUPERFICIE);
    c.getTitle().setPaint(Tema.TEXTO);
    if (c.getLegend() != null) c.getLegend().setBackgroundPaint(Tema.SUPERFICIE);
    if (c.getLegend() != null) c.getLegend().setItemPaint(Tema.TEXTO);
    if (c.getPlot() instanceof CategoryPlot p) {
      p.setBackgroundPaint(Tema.SUPERFICIE);
      p.setDomainGridlinePaint(new Color(75, 77, 90));
      p.setRangeGridlinePaint(new Color(75, 77, 90));
      p.getDomainAxis().setTickLabelPaint(Tema.TEXTO_SUAVE);
      p.getDomainAxis().setLabelPaint(Tema.TEXTO);
      p.getRangeAxis().setTickLabelPaint(Tema.TEXTO_SUAVE);
      p.getRangeAxis().setLabelPaint(Tema.TEXTO);
    }
  }

  private String moeda(BigDecimal v) {
    return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(v);
  }
}
