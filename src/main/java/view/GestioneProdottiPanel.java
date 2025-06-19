package view;

import controller.ProductController;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Pannello grafico per la gestione dei prodotti.
 * Permette di filtrare, aggiungere, modificare ed eliminare prodotti dal catalogo.
 */
@SuppressWarnings("serial")
public class GestioneProdottiPanel extends JPanel {
    private JTable tabellaProdotti;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton bottoneAggiungi, bottoneModifica, bottoneElimina, bottoneFiltra;
    private JLabel messaggio;

    private JTextField filtroNome;
    private JComboBox<String> filtroCategoria;
    private JSpinner spinnerPrezzoMin, spinnerPrezzoMax;

    private final String[] categorie = {
        "Tutte",
        "Frutta", "Verdura", "Carne", "Pesce", "Pane e prodotti da forno", "Latticini e Uova",
        "Salumi e Affettati", "Surgelati", "Bevande", "Dolci, Snack e Pasticceria", "Pasta, Riso e Cereali",
        "Prodotti in scatola e Conserve", "Condimenti e Spezie", "Prodotti per la Colazione",
        "Alimenti per bambini", "Cura della persona", "Pulizia casa", "Alimenti per animali"
    };

    // Costruisce il pannello per la gestione dei prodotti.
    @SuppressWarnings("unused")
	public GestioneProdottiPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titolo = new JLabel("Gestione Prodotti", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titolo);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Barra dei filtri: permette di ricercare per nome, categoria e fascia di prezzo
        JPanel filtroPanel = new JPanel();
        filtroPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2));
        filtroNome = new JTextField(8);
        filtroCategoria = new JComboBox<>(categorie);

        spinnerPrezzoMin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 0.5));
        spinnerPrezzoMax = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 0.5));
        JSpinner.NumberEditor editorMin = new JSpinner.NumberEditor(spinnerPrezzoMin, "#.##");
        JSpinner.NumberEditor editorMax = new JSpinner.NumberEditor(spinnerPrezzoMax, "#.##");
        spinnerPrezzoMin.setEditor(editorMin);
        spinnerPrezzoMax.setEditor(editorMax);
        editorMin.getTextField().setText("");
        editorMax.getTextField().setText("");
        // Spinner prezzo più larghi per migliore usabilità
        spinnerPrezzoMin.setPreferredSize(new Dimension(70, 24));
        spinnerPrezzoMax.setPreferredSize(new Dimension(70, 24));

        bottoneFiltra = new JButton("Filtra");
        filtroNome.setFont(new Font("Arial", Font.PLAIN, 13));
        filtroCategoria.setFont(new Font("Arial", Font.PLAIN, 13));
        editorMin.getTextField().setFont(new Font("Arial", Font.PLAIN, 13));
        editorMax.getTextField().setFont(new Font("Arial", Font.PLAIN, 13));
        bottoneFiltra.setFont(new Font("Arial", Font.PLAIN, 13));

        filtroPanel.add(new JLabel("Nome:"));
        filtroPanel.add(filtroNome);
        filtroPanel.add(new JLabel("Cat.:"));
        filtroPanel.add(filtroCategoria);
        filtroPanel.add(new JLabel("€:"));
        filtroPanel.add(spinnerPrezzoMin);
        filtroPanel.add(new JLabel("-"));
        filtroPanel.add(spinnerPrezzoMax);
        filtroPanel.add(bottoneFiltra);
        add(filtroPanel);
        add(Box.createRigidArea(new Dimension(0, 6)));

        // Modello di tabella: colonne e tipi di dati per ordinamento corretto
        String[] colonne = {"ID", "Nome", "Categoria", "Quantità", "Prezzo"};
        DefaultTableModel modello = new DefaultTableModel(colonne, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID
                if (columnIndex == 3) return Integer.class; // Quantità
                if (columnIndex == 4) return Double.class;  // Prezzo
                return String.class;
            }
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabellaProdotti = new JTable(modello);

        // Abilita ordinamento sulle colonne della tabella
        sorter = new TableRowSorter<>(modello);
        tabellaProdotti.setRowSorter(sorter);

        tabellaProdotti.setRowHeight(26);
        tabellaProdotti.setFont(new Font("Arial", Font.PLAIN, 15));
        tabellaProdotti.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(tabellaProdotti);
        scrollPane.setPreferredSize(new Dimension(400, 140));
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 8)));

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
        tabellaProdotti.getColumnModel().getColumn(4).setCellRenderer(prezzoRenderer);

        // Etichetta per messaggi di stato o errore
        messaggio = new JLabel(" ", SwingConstants.CENTER);
        messaggio.setForeground(Color.BLUE);
        messaggio.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(messaggio);
        add(Box.createRigidArea(new Dimension(0, 8)));

        // Pannello dei bottoni di gestione prodotto
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        bottoneAggiungi = new JButton("Aggiungi");
        bottoneAggiungi.setPreferredSize(new Dimension(100, 28));
        bottoneAggiungi.setFocusPainted(false);
        bottoneModifica = new JButton("Modifica");
        bottoneModifica.setPreferredSize(new Dimension(100, 28));
        bottoneModifica.setFocusPainted(false);
        bottoneElimina = new JButton("Elimina");
        bottoneElimina.setPreferredSize(new Dimension(100, 28));
        bottoneElimina.setFocusPainted(false);
        bottoniPanel.add(bottoneAggiungi);
        bottoniPanel.add(bottoneModifica);
        bottoniPanel.add(bottoneElimina);
        add(bottoniPanel);

        // Carica prodotti senza filtri all'avvio
        caricaProdotti(null, "Tutte", null, null);

        // Listener per il filtro prodotti
        bottoneFiltra.addActionListener(e -> {
            String nome = filtroNome.getText().trim();
            String categoria = (String) filtroCategoria.getSelectedItem();

            String prezzoMinStr = ((JSpinner.NumberEditor) spinnerPrezzoMin.getEditor()).getTextField().getText().trim();
            String prezzoMaxStr = ((JSpinner.NumberEditor) spinnerPrezzoMax.getEditor()).getTextField().getText().trim();
            Double prezzoMin = null, prezzoMax = null;
            try {
                if (!prezzoMinStr.isEmpty()) prezzoMin = Double.parseDouble(prezzoMinStr.replace(",", "."));
                if (!prezzoMaxStr.isEmpty()) prezzoMax = Double.parseDouble(prezzoMaxStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                messaggio.setText("Prezzo minimo/massimo non valido.");
                return;
            }
            caricaProdotti(nome, categoria, prezzoMin, prezzoMax);
        });

        // Listener per aggiungere un nuovo prodotto
        bottoneAggiungi.addActionListener((ActionEvent e) -> {
            JTextField nome = new JTextField();
            JComboBox<String> categoria = new JComboBox<>(categorie);
            JTextField quantita = new JTextField();
            JTextField prezzo = new JTextField();
            Object[] fields = {"Nome:", nome, "Categoria:", categoria, "Quantità:", quantita, "Prezzo:", prezzo};
            int res = JOptionPane.showConfirmDialog(this, fields, "Aggiungi Prodotto", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String n = nome.getText().trim();
                    String cat = (String) categoria.getSelectedItem();
                    int q = Integer.parseInt(quantita.getText().trim());
                    BigDecimal pr = new BigDecimal(prezzo.getText().trim().replace(",", "."));
                    ProductController productController = new ProductController();
                    Product p = new Product(0, n, q, pr, cat);
                    productController.addProduct(p);
                    messaggio.setText("Prodotto aggiunto!");
                    caricaProdotti(null, "Tutte", null, null);
                } catch (Exception ex) {
                    messaggio.setText("Dati non validi.");
                }
            }
        });

        // Listener per modificare un prodotto esistente
        bottoneModifica.addActionListener((ActionEvent e) -> {
            int viewRow = tabellaProdotti.getSelectedRow();
            if (viewRow == -1) {
                messaggio.setText("Seleziona un prodotto da modificare!");
                return;
            }
            int row = tabellaProdotti.convertRowIndexToModel(viewRow);
            DefaultTableModel modelloTab = (DefaultTableModel) tabellaProdotti.getModel();
            int id = (int) modelloTab.getValueAt(row, 0);
            String nomeOld = (String) modelloTab.getValueAt(row, 1);
            String categoriaOld = (String) modelloTab.getValueAt(row, 2);
            int quantitaOld = (int) modelloTab.getValueAt(row, 3);
            double prezzoOld = (double) modelloTab.getValueAt(row, 4);

            JTextField nome = new JTextField(nomeOld);
            JComboBox<String> categoria = new JComboBox<>(categorie);
            categoria.setSelectedItem(categoriaOld);
            JTextField quantita = new JTextField(String.valueOf(quantitaOld));
            JTextField prezzo = new JTextField(String.valueOf(prezzoOld));
            Object[] fields = {"Nome:", nome, "Categoria:", categoria, "Quantità:", quantita, "Prezzo:", prezzo};
            int res = JOptionPane.showConfirmDialog(this, fields, "Modifica Prodotto", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String n = nome.getText().trim();
                    String cat = (String) categoria.getSelectedItem();
                    int q = Integer.parseInt(quantita.getText().trim());
                    BigDecimal pr = new BigDecimal(prezzo.getText().trim().replace(",", "."));
                    ProductController productController = new ProductController();
                    Product p = new Product(id, n, q, pr, cat);
                    productController.updateProduct(p);
                    messaggio.setText("Prodotto modificato!");
                    caricaProdotti(null, "Tutte", null, null);
                } catch (Exception ex) {
                    messaggio.setText("Dati non validi.");
                }
            }
        });

        // Listener per eliminare un prodotto selezionato
        bottoneElimina.addActionListener((ActionEvent e) -> {
            int viewRow = tabellaProdotti.getSelectedRow();
            if (viewRow == -1) {
                messaggio.setText("Seleziona un prodotto da eliminare!");
                return;
            }
            int row = tabellaProdotti.convertRowIndexToModel(viewRow);
            DefaultTableModel modelloTab = (DefaultTableModel) tabellaProdotti.getModel();
            int id = (int) modelloTab.getValueAt(row, 0);
            int conferma = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler eliminare il prodotto?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conferma == JOptionPane.YES_OPTION) {
                ProductController productController = new ProductController();
                productController.deleteProduct(id);
                messaggio.setText("Prodotto eliminato!");
                caricaProdotti(null, "Tutte", null, null);
            }
        });
    }

    /**
     * Carica i prodotti applicando eventuali filtri.
     * Se uno dei parametri è null o "Tutte", mostra tutti i prodotti.
     */
    private void caricaProdotti(String nome, String categoria, Double prezzoMin, Double prezzoMax) {
        ProductController productController = new ProductController();
        DefaultTableModel modello = (DefaultTableModel) tabellaProdotti.getModel();
        modello.setRowCount(0);
        List<Product> prodotti;
        try {
            // Valori di default se parametri sono null
            String nomeFiltro = (nome == null) ? "" : nome;
            String categoriaFiltro = (categoria == null) ? "Tutte" : categoria;

            double prezzoFiltroMin = 0.0;
            double prezzoFiltroMax = 1_000_000.0;
            if (prezzoMin != null) prezzoFiltroMin = prezzoMin;
            if (prezzoMax != null) prezzoFiltroMax = prezzoMax;

            prodotti = productController.cercaProdotti(nomeFiltro, categoriaFiltro, prezzoFiltroMin, prezzoFiltroMax);
            if (prodotti != null) {
                for (Product p : prodotti) {
                    modello.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getQuantity(),
                        p.getPrice().doubleValue()
                    });
                }
            }
        } catch (Exception ex) {
            messaggio.setText("Errore durante il caricamento dei prodotti.");
        }
    }
}