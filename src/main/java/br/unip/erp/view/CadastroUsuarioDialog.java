package br.unip.erp.view;

import br.unip.erp.controller.UsuarioController;
import br.unip.erp.model.Usuario;
import br.unip.erp.util.Tema;
import java.awt.*;
import javax.swing.*;

/** Cadastro público seguro: sempre cria usuário comum e ativo. */
public class CadastroUsuarioDialog extends JDialog {
  private final JTextField nome = new JTextField(22), login = new JTextField(18);
  private final JPasswordField senha = new JPasswordField(18), confirmacao = new JPasswordField(18);

  public CadastroUsuarioDialog(Frame owner) {
    super(owner, "Criar usuário", true);
    setSize(390, 300);
    setLocationRelativeTo(owner);
    JPanel p = new JPanel(new GridLayout(5, 2, 8, 8));
    p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    p.add(new JLabel("Nome:"));
    p.add(nome);
    p.add(new JLabel("Login:"));
    p.add(login);
    p.add(new JLabel("Senha:"));
    p.add(senha);
    p.add(new JLabel("Confirmar senha:"));
    p.add(confirmacao);
    JButton criar = new JButton("Criar conta");
    Tema.botaoPrimario(criar);
    criar.addActionListener(e -> criar());
    p.add(new JLabel("Perfil: usuário comum"));
    p.add(criar);
    setContentPane(p);
  }

  private void criar() {
    String s = new String(senha.getPassword());
    if (!s.equals(new String(confirmacao.getPassword()))) {
      JOptionPane.showMessageDialog(this, "As senhas não conferem.");
      return;
    }
    try {
      Usuario u = new Usuario();
      u.setNome(nome.getText().trim());
      u.setLogin(login.getText().trim());
      u.setSenha(s);
      new UsuarioController().cadastrarPublico(u);
      JOptionPane.showMessageDialog(this, "Usuário criado. Faça o login.");
      dispose();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
  }
}
