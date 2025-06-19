package view;

import controller.ProductController;
import controller.CartController;
import model.Product;
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
 * Pannello grafico per la visualizzazione del catalogo prodotti.
 * Permette la ricerca e il filtraggio dei prodotti, l'aggiunta al carrello e l'eliminazione (solo admin).
 */
@SuppressWarnings("serial")
public class VisualizzaProdottiPanel extends JPanel {
    private JTable tabellaProdotti;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton bottoneAggiungiCarrello, bottoneEliminaProdotto;
    private JLabel messaggioInfo;
    @SuppressWarnings("unused")
	private User utente;

    // Lista dei prodotti attualmente visualizzati (per gestire operazioni sui dati)
    private List<Product> prodottiCorrenti;

    // Campi filtro
    private JTextField campoNome;
    private JComboBox<String> comboCategoria;
    private JSpinner spinnerPrezzoMin, spinnerPrezzoMax;
    private JButton bottoneFiltra;

    // Costruttore che inizializza il pannello catalogo prodotti.
    @SuppressWarnings("unused")
	public VisualizzaProdottiPanel(User utente) {
        this.utente = utente;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titolo = new JLabel("Catalogo Prodotti", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titolo);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Barra filtri prodotti (nome, categoria, prezzi)
        JPanel filtriPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        campoNome = new JTextField(8);
        comboCategoria = new JComboBox<>(new String[]{
            "Tutte", "Frutta", "Verdura", "Carne", "Pesce", "Pane e prodotti da forno", "Latticini e Uova",
            "Salumi e Affettati", "Surgelati", "Bevande", "Dolci, Snack e Pasticceria", "Pasta, Riso e Cereali",
            "Prodotti in scatola e Conserve", "Condimenti e Spezie", "Prodotti per la Colazione",
            "Alimenti per bambini", "Cura della persona", "Pulizia casa", "Alimenti per animali"
        });

        spinnerPrezzoMin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 0.5));
        spinnerPrezzoMax = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 0.5));
        JSpinner.NumberEditor editorMin = new JSpinner.NumberEditor(spinnerPrezzoMin, "#.##");
        JSpinner.NumberEditor editorMax = new JSpinner.NumberEditor(spinnerPrezzoMax, "#.##");
        spinnerPrezzoMin.setEditor(editorMin);
        spinnerPrezzoMax.setEditor(editorMax);
        editorMin.getTextField().setText("");
        editorMax.getTextField().setText("");
        spinnerPrezzoMin.setPreferredSize(new Dimension(70, 24));
        spinnerPrezzoMax.setPreferredSize(new Dimension(70, 24));

        bottoneFiltra = new JButton("Filtra");
        campoNome.setFont(new Font("Arial", Font.PLAIN, 13));
        comboCategoria.setFont(new Font("Arial", Font.PLAIN, 13));
        editorMin.getTextField().setFont(new Font("Arial", Font.PLAIN, 13));
        editorMax.getTextField().setFont(new Font("Arial", Font.PLAIN, 13));
        bottoneFiltra.setFont(new Font("Arial", Font.PLAIN, 13));

        filtriPanel.add(new JLabel("Nome:"));
        filtriPanel.add(campoNome);
        filtriPanel.add(new JLabel("Cat.:"));
        filtriPanel.add(comboCategoria);
        filtriPanel.add(new JLabel("€:"));
        filtriPanel.add(spinnerPrezzoMin);
        filtriPanel.add(new JLabel("-"));
        filtriPanel.add(spinnerPrezzoMax);
        filtriPanel.add(bottoneFiltra);

        add(filtriPanel);

        // Modello di tabella con classi colonna corrette per ordinamento numerico
        String[] colonne = {"Nome", "Categoria", "Quantità", "Prezzo"};
        DefaultTableModel modello = new DefaultTableModel(colonne, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class; // Quantità
                if (columnIndex == 3) return Double.class;   // Prezzo
                return String.class;
            }
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabellaProdotti = new JTable(modello);

        // Abilita ordinamento sulle colonne
        sorter = new TableRowSorter<>(modello);
        tabellaProdotti.setRowSorter(sorter);

        // Selezione multipla per aggiunta multipla al carrello
        tabellaProdotti.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabellaProdotti.setRowHeight(26);
        tabellaProdotti.setFont(new Font("Arial", Font.PLAIN, 15));
        tabellaProdotti.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(tabellaProdotti);
        scrollPane.setPreferredSize(new Dimension(500, 140));
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Messaggio informativo (esito operazioni)
        messaggioInfo = new JLabel(" ", SwingConstants.CENTER);
        messaggioInfo.setForeground(new Color(0, 128, 0));
        messaggioInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(messaggioInfo);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Bottone per aggiungere prodotti selezionati al carrello
        bottoneAggiungiCarrello = new JButton("Aggiungi al Carrello");
        bottoneAggiungiCarrello.setPreferredSize(new Dimension(180, 28));
        bottoneAggiungiCarrello.setFocusPainted(false);
        bottoneAggiungiCarrello.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(bottoneAggiungiCarrello);

        // Bottone elimina prodotto (solo per admin)
        if (utente != null && utente.isAdmin()) {
            bottoneEliminaProdotto = new JButton("Elimina Prodotto");
            bottoneEliminaProdotto.setPreferredSize(new Dimension(180, 28));
            bottoneEliminaProdotto.setFocusPainted(false);
            bottoneEliminaProdotto.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(Box.createRigidArea(new Dimension(0, 8)));
            add(bottoneEliminaProdotto);

            // Listener per eliminazione prodotto (admin)
            bottoneEliminaProdotto.addActionListener((ActionEvent e) -> {
                int row = tabellaProdotti.getSelectedRow();
                if (row == -1) {
                    messaggioInfo.setText("Seleziona un prodotto da eliminare!");
                    return;
                }
                int modelRow = tabellaProdotti.convertRowIndexToModel(row);
                if (prodottiCorrenti == null || modelRow >= prodottiCorrenti.size()) {
                    messaggioInfo.setText("Errore interno. Riprova.");
                    return;
                }
                Product prodottoSel = prodottiCorrenti.get(modelRow);
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Sei sicuro di voler eliminare il prodotto \"" + prodottoSel.getName() + "\"?",
                        "Conferma eliminazione",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    ProductController productController = new ProductController();
                    productController.deleteProduct(prodottoSel.getId());
                    messaggioInfo.setText("Prodotto eliminato!");
                    caricaProdottiFiltrati();
                }
            });
        }

        // Carica l'elenco prodotti iniziale
        caricaProdottiFiltrati();

        // Listener per filtri
        bottoneFiltra.addActionListener((ActionEvent e) -> caricaProdottiFiltrati());

        // Listener per aggiunta al carrello di uno o più prodotti selezionati
        bottoneAggiungiCarrello.addActionListener((ActionEvent e) -> {
            int[] rows = tabellaProdotti.getSelectedRows();
            if (rows.length == 0) {
                messaggioInfo.setText("Seleziona almeno un prodotto!");
                return;
            }
            boolean almenoUnoAggiunto = false;
            for (int row : rows) {
                // Con sorter attivo, converti l'indice della view all'indice del modello!
                int modelRow = tabellaProdotti.convertRowIndexToModel(row);
                if (prodottiCorrenti == null || modelRow >= prodottiCorrenti.size()) {
                    messaggioInfo.setText("Errore interno. Riprova.");
                    continue;
                }
                Product prodottoSel = prodottiCorrenti.get(modelRow);
                int productId = prodottoSel.getId();
                String nome = prodottoSel.getName();
                int maxQty = prodottoSel.getQuantity();

                if (maxQty == 0) {
                    messaggioInfo.setText("Prodotto non disponibile: " + nome);
                    continue;
                }

                String qtyStr = JOptionPane.showInputDialog(this, "Quanti \"" + nome + "\" vuoi aggiungere al carrello?", "1");
                if (qtyStr == null) continue;
                int qty;
                try {
                    qty = Integer.parseInt(qtyStr);
                    if (qty <= 0 || qty > maxQty) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    messaggioInfo.setText("Quantità non valida per " + nome);
                    continue;
                }
                CartController cartController = new CartController();
                if (cartController.addToCart(utente.getId(), productId, qty)) {
                    almenoUnoAggiunto = true;
                } else {
                    messaggioInfo.setText("Errore, quantità non disponibile per " + nome);
                }
            }
            if (almenoUnoAggiunto) {
                messaggioInfo.setText("Prodotti aggiunti al carrello!");
                caricaProdottiFiltrati();
            }
        });
    }

    /**
     * Carica i prodotti applicando i filtri selezionati e aggiorna la tabella.
     * Può essere chiamato anche dall'esterno per aggiornare la lista prodotti.
     */
    public void caricaProdottiFiltrati() {
        ProductController productController = new ProductController();
        DefaultTableModel modello = (DefaultTableModel) tabellaProdotti.getModel();
        modello.setRowCount(0);

        String nome = campoNome.getText().trim();
        String categoria = (String) comboCategoria.getSelectedItem();

        String prezzoMinStr = ((JSpinner.NumberEditor) spinnerPrezzoMin.getEditor()).getTextField().getText().trim();
        String prezzoMaxStr = ((JSpinner.NumberEditor) spinnerPrezzoMax.getEditor()).getTextField().getText().trim();
        double prezzoMin = prezzoMinStr.isEmpty() ? 0.0 : Double.parseDouble(prezzoMinStr.replace(",", "."));
        double prezzoMax = prezzoMaxStr.isEmpty() ? 1_000_000.0 : Double.parseDouble(prezzoMaxStr.replace(",", "."));

        prodottiCorrenti = productController.cercaProdotti(nome, categoria, prezzoMin, prezzoMax);
        if (prodottiCorrenti != null) {
            for (Product p : prodottiCorrenti) {
                modello.addRow(new Object[]{
                        p.getName(),
                        p.getCategory() != null ? p.getCategory() : "",
                        p.getQuantity(),
                        p.getPrice() // Double nel modello!
                });
            }
        }

        // Renderer per aggiungere il simbolo euro SOLO in visualizzazione
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
        tabellaProdotti.getColumnModel().getColumn(3).setCellRenderer(prezzoRenderer);
    }
}