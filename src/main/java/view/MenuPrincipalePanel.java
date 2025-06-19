package view;

import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale del menu Cliente.
 * Permette la navigazione tra prodotti, carrello, ordini e logout per l'utente autenticato.
 */
@SuppressWarnings("serial")
public class MenuPrincipalePanel extends JFrame {
    @SuppressWarnings("unused")
	private User utente;
    private JPanel panelContenuti;

    // Costruttore che inizializza la finestra menu per il cliente.
    @SuppressWarnings("unused")
	public MenuPrincipalePanel(User utente) {
        this.utente = utente;
        setTitle("Mini Market - Menu Cliente");
        setSize(700, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Layout principale con BorderLayout
        setLayout(new BorderLayout());

        // Pannello pulsanti navigazione (in alto)
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));
        menuPanel.setBackground(new Color(240, 240, 240));

        JButton btnProdotti = new JButton("Prodotti");
        JButton btnCarrello = new JButton("Carrello");
        JButton btnOrdini = new JButton("Ordini");
        JButton btnLogout = new JButton("Logout");

        for (JButton btn : new JButton[]{btnProdotti, btnCarrello, btnOrdini, btnLogout}) {
            btn.setPreferredSize(new Dimension(130, 34));
            btn.setFont(new Font("Arial", Font.BOLD, 15));
            btn.setFocusPainted(false);
            menuPanel.add(btn);
        }

        add(menuPanel, BorderLayout.NORTH);

        // Pannello centrale per i contenuti
        panelContenuti = new JPanel(new BorderLayout());
        add(panelContenuti, BorderLayout.CENTER);

        // Pannelli da mostrare
        VisualizzaProdottiPanel prodottiPanel = new VisualizzaProdottiPanel(utente);
        OrdiniPanel ordiniPanel = new OrdiniPanel(utente);
        CarrelloPanel carrelloPanel = new CarrelloPanel(utente, prodottiPanel, ordiniPanel); // per aggiornare il pannello prodotti e ordini

        // Mostra per default i prodotti
        mostraContenuto(prodottiPanel);

        // Azioni pulsanti
        btnProdotti.addActionListener(e -> mostraContenuto(prodottiPanel));
        btnCarrello.addActionListener(e -> {
            carrelloPanel.refresh();
            mostraContenuto(carrelloPanel);
        });
        btnOrdini.addActionListener(e -> {
            ordiniPanel.refresh();
            mostraContenuto(ordiniPanel);
        });
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