package view;

import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale del menu Admin.
 * Consente all'amministratore di gestire prodotti, visualizzare ordini dei clienti e effettuare il logout.
 */
@SuppressWarnings("serial")
public class MenuAdminPanel extends JFrame {
    @SuppressWarnings("unused")
	private User utente;
    private JPanel panelContenuti;

    // Costruttore che inizializza la finestra menu per l'amministratore.
    @SuppressWarnings("unused")
	public MenuAdminPanel(User utente) {
        this.utente = utente;
        setTitle("Mini Market - Menu Admin");
        setSize(700, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());

        // Pannello superiore con i pulsanti di navigazione
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));
        menuPanel.setBackground(new Color(240, 240, 240));

        JButton btnGestioneProdotti = new JButton("Gestione Prodotti");
        JButton btnOrdiniClienti = new JButton("Ordini Clienti");
        JButton btnLogout = new JButton("Logout");

        for (JButton btn : new JButton[]{btnGestioneProdotti, btnOrdiniClienti, btnLogout}) {
            btn.setPreferredSize(new Dimension(180, 34));
            btn.setFont(new Font("Arial", Font.BOLD, 15));
            btn.setFocusPainted(false);
            menuPanel.add(btn);
        }

        add(menuPanel, BorderLayout.NORTH);

        // Pannello centrale dove vengono mostrati i contenuti
        panelContenuti = new JPanel(new BorderLayout());
        add(panelContenuti, BorderLayout.CENTER);

        GestioneProdottiPanel gestionePanel = new GestioneProdottiPanel();
        OrdiniPanel ordiniPanel = new OrdiniPanel(utente); // L'utente admin può vedere tutti gli ordini

        mostraContenuto(gestionePanel);

        btnGestioneProdotti.addActionListener(e -> mostraContenuto(gestionePanel));
        btnOrdiniClienti.addActionListener(e -> mostraContenuto(ordiniPanel));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler uscire?",
                "Conferma Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPanel().setVisible(true);
            }
        });
    }

    // Mostra il pannello passato come contenuto principale della finestra.
    private void mostraContenuto(JPanel nuovo) {
        panelContenuti.removeAll();
        panelContenuti.add(nuovo, BorderLayout.CENTER);
        panelContenuti.revalidate();
        panelContenuti.repaint();
    }
}