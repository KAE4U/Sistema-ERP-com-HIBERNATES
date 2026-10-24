package br.unip.erp.view;

import br.unip.erp.service.ReportService;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import javax.swing.*;

final class ReportActions {
  private ReportActions() {}

  static JButton botao(Component parent, ReportService.Tipo tipo) {
    JButton b = new JButton("Relatório PDF");
    b.addActionListener(e -> gerar(parent, tipo));
    return b;
  }

  static void gerar(Component parent, ReportService.Tipo tipo) {
    new SwingWorker<Path, Void>() {
      protected Path doInBackground() {
        return new ReportService().gerar(tipo);
      }

      protected void done() {
        try {
          JOptionPane.showMessageDialog(parent, "PDF gerado em:\n" + get());
        } catch (Exception e) {
          JOptionPane.showMessageDialog(parent, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
      }
    }.execute();
  }

  static void gerarTodos(Component parent) {
    new SwingWorker<List<Path>, Void>() {
      protected List<Path> doInBackground() {
        return new ReportService().gerarTodos();
      }

      protected void done() {
        try {
          JOptionPane.showMessageDialog(
              parent, get().size() + " relatórios gerados na pasta relatorios/.");
        } catch (Exception e) {
          JOptionPane.showMessageDialog(parent, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
      }
    }.execute();
  }
}
