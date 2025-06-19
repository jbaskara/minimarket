package view;

import controller.CartController;
import model.CartItem;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Pannello grafico per la visualizzazione e gestione del carrello dell'utente.
 * Permette di rimuovere prodotti, visualizzare il totale e finalizzare l'acquisto.
 */
@SuppressWarnings("serial")
public class CarrelloPanel extends JPanel {
    private JTable tabellaCarrello;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton bottoneRimuovi, bottoneAcquista;
    private JLabel labelTotale, messaggioErrore;
    private User utente;

    // Lista degli oggetti attualmente presenti nel carrello (per gestire le operazioni sui dati)
    private List<CartItem> itemsCorrenti;

    // Riferimento al pannello prodotti per aggiornamento dopo rimozione/acquisto
    @SuppressWarnings("unused")
	private VisualizzaProdottiPanel visualizzaProdottiPanel;

    // Riferimento al pannello storico ordini per aggiornamento dopo acquisto
    @SuppressWarnings("unused")
	private OrdiniPanel ordiniPanel;

    // Costruisce il pannello del carrello per l'utente specificato.
    @SuppressWarnings("unused")
	public CarrelloPanel(User utente, VisualizzaProdottiPanel visualizzaProdottiPanel, OrdiniPanel ordiniPanel) {
        this.utente = utente;
        this.visualizzaProdottiPanel = visualizzaProdottiPanel;
        this.ordiniPanel = ordiniPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titolo = new JLabel("Il tuo Carrello", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titolo);
        add(Box.createRigidArea(new Dimension(0, 16)));

        // Modello di tabella con tipi di dati impostati per ordinamento corretto
        String[] colonne = {"Prodotto", "Quantità", "Prezzo"};
        DefaultTableModel modello = new DefaultTableModel(colonne, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class; // Quantità
                if (columnIndex == 2) return Double.class;   // Prezzo
                return String.class;
            }
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabellaCarrello = new JTable(modello);

        // Abilita ordinamento sulle colonne della tabella
        sorter = new TableRowSorter<>(modello);
        tabellaCarrello.setRowSorter(sorter);

        // Permette la selezione multipla delle righe
        tabellaCarrello.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabellaCarrello.setRowHeight(26);
        tabellaCarrello.setFont(new Font("Arial", Font.PLAIN, 15));
        tabellaCarrello.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(tabellaCarrello);
        scrollPane.setPreferredSize(new Dimension(340, 120));
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Etichetta per il totale del carrello
        labelTotale = new JLabel("Totale: 0.00 €", SwingConstants.CENTER);
        labelTotale.setFont(new Font("Arial", Font.BOLD, 16));
        labelTotale.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(labelTotale);
        add(Box.createRigidArea(new Dimension(0, 4)));

        // Etichetta per messaggi d'errore o conferma
        messaggioErrore = new JLabel(" ", SwingConstants.CENTER);
        messaggioErrore.setForeground(Color.RED);
        messaggioErrore.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(messaggioErrore);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Pannello dei bottoni "Rimuovi" e "Acquista"
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        bottoneRimuovi = new JButton("Rimuovi");
        bottoneRimuovi.setPreferredSize(new Dimension(120, 28));
        bottoneRimuovi.setFocusPainted(false);
        bottoneAcquista = new JButton("Acquista");
        bottoneAcquista.setPreferredSize(new Dimension(120, 28));
        bottoneAcquista.setFocusPainted(false);
        bottoniPanel.add(bottoneRimuovi);
        bottoniPanel.add(bottoneAcquista);
        add(bottoniPanel);

        // Carica inizialmente i dati del carrello
        caricaCarrello();

        // Listener per la rimozione di prodotti dal carrello
        bottoneRimuovi.addActionListener((ActionEvent e) -> {
            messaggioErrore.setText(" "); // reset messaggio precedente
            int[] selectedRows = tabellaCarrello.getSelectedRows();
            if (selectedRows.length == 0) {
                messaggioErrore.setText("Seleziona uno o più prodotti da rimuovere!");
                return;
            }
            CartController cartController = new CartController();
            boolean almenoUnoRimosso = false;
            // Rimuovi dall'ultimo al primo (per evitare problemi di shift degli indici
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int viewRow = selectedRows[i];
                int modelRow = tabellaCarrello.convertRowIndexToModel(viewRow);
                if (itemsCorrenti == null || modelRow >= itemsCorrenti.size()) continue;
                int cartId = itemsCorrenti.get(modelRow).getId();
                if (cartController.removeFromCart(cartId)) {
                    almenoUnoRimosso = true;
                }
            }
            if (almenoUnoRimosso) {
                messaggioErrore.setText("Prodotti rimossi dal carrello.");
                caricaCarrello();
                // Aggiorna la tabella prodotti dopo la rimozione dal carrello
                if (visualizzaProdottiPanel != null) {
                    visualizzaProdottiPanel.caricaProdottiFiltrati();
                }
            } else {
                messaggioErrore.setText("Errore nella rimozione.");
            }
        });

        // Listener per l'acquisto dei prodotti nel carrello, con dialog di conferma
        bottoneAcquista.addActionListener((ActionEvent e) -> {
            messaggioErrore.setText(" "); // reset messaggio precedente
            DefaultTableModel modelAcquista = (DefaultTableModel) tabellaCarrello.getModel();
            if (modelAcquista.getRowCount() == 0) {
                messaggioErrore.setText("Il carrello è vuoto!");
                return;
            }
            // Dialog di conferma prima dell'acquisto
            int conferma = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler procedere all'acquisto dei prodotti nel carrello?",
                "Conferma Acquisto",
                JOptionPane.YES_NO_OPTION
            );
            if (conferma != JOptionPane.YES_OPTION) {
                // Utente ha cliccato "No" o ha chiuso la dialog: non fare nulla
                return;
            }
            CartController cartController = new CartController();
            if (cartController.purchaseCart(utente.getId())) {
                JOptionPane.showMessageDialog(this, "Acquisto effettuato!");
                caricaCarrello();
                // Aggiorna la tabella prodotti dopo l'acquisto
                if (visualizzaProdottiPanel != null) {
                    visualizzaProdottiPanel.caricaProdottiFiltrati();
                }
                // Aggiorna lo storico ordini dopo l'acquisto
                if (ordiniPanel != null) {
                    ordiniPanel.refresh();
                }
            } else {
                messaggioErrore.setText("Errore nell'acquisto.");
            }
        });

        // Renderer per la colonna prezzo: aggiunge il simbolo € e mostra due decimali
        DefaultTableCellRenderer prezzoRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number) {
                    setText(String.format("%.2f €", ((Number)value).doubleValue()));
                } else {
                    setText(value != null ? value.toString() : "");
                }
            }
        };
        tabellaCarrello.getColumnModel().getColumn(2).setCellRenderer(prezzoRenderer);
    }

    // Carica gli oggetti del carrello corrente e aggiorna la tabella e il totale.
    private void caricaCarrello() {
        CartController cartController = new CartController();
        DefaultTableModel modello = (DefaultTableModel) tabellaCarrello.getModel();
        modello.setRowCount(0);
        itemsCorrenti = cartController.getCartItems(utente.getId());
        double totale = 0;
        if (itemsCorrenti != null) {
            for (CartItem item : itemsCorrenti) {
                // Inserisci quantità (int) e prezzo (double) per ordinamento corretto
                modello.addRow(new Object[]{item.getProductName(), item.getQuantity(), item.getPrice().doubleValue()});
                totale += item.getPrice().doubleValue() * item.getQuantity();
            }
        }
        labelTotale.setText("Totale: " + String.format("%.2f", totale) + " €");
    }

    // Aggiorna il contenuto del carrello. Può essere chiamato dall'esterno.
    public void refresh() {
        caricaCarrello();
    }
}