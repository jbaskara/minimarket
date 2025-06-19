package view;

import controller.AuthController;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Finestra per la registrazione di un nuovo utente.
 * Permette l'inserimento dei dati personali e la creazione di un nuovo account.
 * Per creare un account per admin, impostare da false a true nella riga 111,
 */
@SuppressWarnings("serial")
public class RegistrazionePanel extends JFrame {
    private JTextField campoUsername, campoNome, campoCognome, campoEmail;
    private JPasswordField campoPassword;
    private JButton bottoneRegistra, bottoneIndietro;
    private JLabel messaggioErrore;

    // Costruttore che inizializza la finestra di registrazione e i suoi componenti grafici.
    @SuppressWarnings("unused")
	public RegistrazionePanel() {
        setTitle("Mini Market - Registrazione");
        setSize(430, 330);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titolo = new JLabel("Registrazione", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titolo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Pannello per i campi di inserimento dati
        JPanel campiPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        campiPanel.setOpaque(false);

        campiPanel.add(new JLabel("Username:"));
        campoUsername = new JTextField();
        campiPanel.add(campoUsername);

        campiPanel.add(new JLabel("Nome:"));
        campoNome = new JTextField();
        campiPanel.add(campoNome);

        campiPanel.add(new JLabel("Cognome:"));
        campoCognome = new JTextField();
        campiPanel.add(campoCognome);

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

        // Pannello bottoni "Registra" e "Indietro"
        JPanel bottoniPanel = new JPanel();
        bottoniPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 24, 0));

        bottoneRegistra = new JButton("Registra");
        bottoneRegistra.setPreferredSize(new Dimension(120, 28));
        bottoneRegistra.setFocusPainted(false);

        bottoneIndietro = new JButton("Indietro");
        bottoneIndietro.setPreferredSize(new Dimension(120, 28));
        bottoneIndietro.setFocusPainted(false);

        bottoniPanel.add(bottoneRegistra);
        bottoniPanel.add(bottoneIndietro);

        mainPanel.add(bottoniPanel);
        add(mainPanel);

        // Listener per la registrazione dell'utente
        bottoneRegistra.addActionListener((ActionEvent e) -> {
            String username = campoUsername.getText().trim();
            String nome = campoNome.getText().trim();
            String cognome = campoCognome.getText().trim();
            String email = campoEmail.getText().trim();
            String password = new String(campoPassword.getPassword());

            if (username.isEmpty() || nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                messaggioErrore.setText("Compila tutti i campi!");
                return;
            }
            User user = new User();
            user.setUsername(username);
            user.setNome(nome);
            user.setCognome(cognome);
            user.setEmail(email);
            user.setPassword(password);
            user.setAdmin(false); // impostato predefinito come cliente;

            AuthController auth = new AuthController();
            if (auth.register(user)) {
                JOptionPane.showMessageDialog(this, "Registrazione avvenuta con successo! Effettua il login.");
                new LoginPanel().setVisible(true);
                dispose();
            } else {
                messaggioErrore.setText("Email già registrata!");
            }
        });

        // Listener per tornare alla schermata di login
        bottoneIndietro.addActionListener((ActionEvent e) -> {
            new LoginPanel().setVisible(true);
            dispose();
        });
    }
}