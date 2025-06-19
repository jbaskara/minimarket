package view;

import controller.AuthController;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Finestra di login.
 * Permette l'accesso tramite email e password o la registrazione di un nuovo utente.
 */
@SuppressWarnings("serial")
public class LoginPanel extends JFrame {
    private JTextField campoEmail;
    private JPasswordField campoPassword;
    private JButton bottoneLogin;
    private JButton bottoneRegistrati;
    private JLabel messaggioErrore;

    // Costruttore che inizializza la finestra di login e i suoi componenti grafici.
    @SuppressWarnings("unused")
    public LoginPanel() {
        setTitle("Mini Market - Login");
        setSize(400, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titolo = new JLabel("Mini Market", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titolo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Pannello per i campi email e password
        JPanel campiPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        campiPanel.setOpaque(false);

        campiPanel.add(new JLabel("Email:"));
        campoEmail = new JTextField();
        campiPanel.add(campoEmail);

        campiPanel.add(new JLabel("Password:"));
        campoPassword = new JPasswordField();
        campiPanel.add(campoPassword);

        mainPanel.add(campiPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Messaggio di errore o conferma
        messaggioErrore = new JLabel(" ", SwingConstants.CENTER);
        messaggioErrore.setForeground(Color.RED);
        messaggioErrore.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messaggioErrore);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Pannello dei bottoni "Accedi" e "Registrati"
        JPanel bottoniPanel = new JPanel();
        bottoniPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 24, 0));

        bottoneLogin = new JButton("Accedi");
        bottoneLogin.setPreferredSize(new Dimension(120, 28));
        bottoneLogin.setFocusPainted(false);

        bottoneRegistrati = new JButton("Registrati");
        bottoneRegistrati.setPreferredSize(new Dimension(120, 28));
        bottoneRegistrati.setFocusPainted(false);

        bottoniPanel.add(bottoneLogin);
        bottoniPanel.add(bottoneRegistrati);

        mainPanel.add(bottoniPanel);
        add(mainPanel);

        // Listener per il login: verifica i campi e autentica l'utente
        bottoneLogin.addActionListener((ActionEvent e) -> {
            String email = campoEmail.getText().trim();
            String password = new String(campoPassword.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                messaggioErrore.setText("Compila tutti i campi!");
                return;
            }
            AuthController auth = new AuthController();
            User utente = auth.login(email, password);
            if (utente != null) {
                if (utente.isAdmin()) {
                    new MenuAdminPanel(utente).setVisible(true);
                } else {
                    new MenuPrincipalePanel(utente).setVisible(true);
                }
                dispose();
            } else {
                messaggioErrore.setText("Email o password errati!");
            }
        });

        // Listener per la registrazione: apre la finestra di registrazione
        bottoneRegistrati.addActionListener((ActionEvent e) -> {
            new RegistrazionePanel().setVisible(true);
            dispose();
        });
    }
}