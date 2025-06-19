package view;

import controller.OrderController;
import model.Order;
import model.OrderItem;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.time.format.DateTimeFormatter;

/**
 * Pannello grafico per la visualizzazione dello storico ordini per clienti e della lista ordini per admin.
 * Permette di visualizzare i dettagli degli ordini e, per admin, di filtrare per username.
 */
@SuppressWarnings("serial")
public class OrdiniPanel extends JPanel {
    private JTable tabellaOrdini;
    private TableRowSorter<DefaultTableModel> sorterOrdini;
    private JTable tabellaDettaglio;
    private TableRowSorter<DefaultTableModel> sorterDettaglio;
    private JLabel labelDettaglio;
    private User utente;
    private boolean isAdmin;
    private JTextField searchField;		// Campo di ricerca per admin (filtra per username)

    // Costruttore che crea il pannello degli ordini.
    public OrdiniPanel(User utente) {
        this.utente = utente;
        this.isAdmin = utente != null && utente.isAdmin();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 24, 16, 24));

        // Campo ricerca username solo per admin
        if (isAdmin) {
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
            searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            searchPanel.setMaximumSize(new Dimension(350, 36));
            searchPanel.add(new JLabel("Cerca per username: "));
            searchField = new JTextField();
            searchPanel.add(searchField);
            searchPanel.add(Box.createRigidArea(new Dimension(8, 0)));
            add(searchPanel);
            add(Box.createRigidArea(new Dimension(0, 12)));

            // Listener per filtrare in tempo reale la tabella in base all'username inserito
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filtroUsername(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filtroUsername(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filtroUsername(); }
            });
        }

        JLabel titolo = new JLabel(isAdmin ? "Ordini Clienti" : "Storico Ordini", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titolo);
        add(Box.createRigidArea(new Dimension(0, 16)));

        String[] colonne = isAdmin
                ? new String[]{"ID", "Username", "Data", "Totale"}
                : new String[]{"ID", "Data", "Totale"};

        // Modello di tabella per la lista ordini
        DefaultTableModel modello = new DefaultTableModel(colonne, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (isAdmin && columnIndex == 3) return Double.class;
                if (!isAdmin && columnIndex == 2) return Double.class;
                return String.class;
            }
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabellaOrdini = new JTable(modello);

        sorterOrdini = new TableRowSorter<>(modello);
        tabellaOrdini.setRowSorter(sorterOrdini);

        tabellaOrdini.setRowHeight(26);
        tabellaOrdini.setFont(new Font("Arial", Font.PLAIN, 15));
        tabellaOrdini.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(tabellaOrdini);
        scrollPane.setPreferredSize(new Dimension(400, 120));
        add(scrollPane);

        // Renderer per la colonna totale (aggiunge €)
        int colTotale = isAdmin ? 3 : 2;
        DefaultTableCellRenderer totaleRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number) {
                    setText(String.format("%.2f €", ((Number)value).doubleValue()));
                } else {
                    setText(value != null ? value.toString() : "");
                }
            }
        };
        tabellaOrdini.getColumnModel().getColumn(colTotale).setCellRenderer(totaleRenderer);

        labelDettaglio = new JLabel("Seleziona un ordine per vedere i dettagli.", SwingConstants.CENTER);
        labelDettaglio.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(labelDettaglio);

        // Tabella dettagli ordine selezionato
        String[] colonneDett = {"Prodotto", "Quantità", "Prezzo"};
        DefaultTableModel modelloDett = new DefaultTableModel(colonneDett, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class;
                if (columnIndex == 2) return Double.class;
                return String.class;
            }
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabellaDettaglio = new JTable(modelloDett);

        sorterDettaglio = new TableRowSorter<>(modelloDett);
        tabellaDettaglio.setRowSorter(sorterDettaglio);

        tabellaDettaglio.setRowHeight(26);
        tabellaDettaglio.setFont(new Font("Arial", Font.PLAIN, 15));
        tabellaDettaglio.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPaneDett = new JScrollPane(tabellaDettaglio);
        scrollPaneDett.setPreferredSize(new Dimension(400, 80));
        add(scrollPaneDett);

        // Renderer per la colonna prezzo (aggiunge €)
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
        tabellaDettaglio.getColumnModel().getColumn(2).setCellRenderer(prezzoRenderer);

        caricaOrdini();

        // Listener per selezione ordine: mostra i dettagli dell'ordine selezionato
        tabellaOrdini.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int viewRow = tabellaOrdini.getSelectedRow();
                if (viewRow == -1) return;
                int modelRow = tabellaOrdini.convertRowIndexToModel(viewRow);
                int orderId = (int) tabellaOrdini.getModel().getValueAt(modelRow, 0);
                caricaDettaglio(orderId);
            }
        });
    }

    // Carica gli ordini dell'utente o di tutti gli utenti (se admin) nella tabella.
    public void caricaOrdini() {
        OrderController orderController = new OrderController();
        DefaultTableModel modello = (DefaultTableModel) tabellaOrdini.getModel();
        modello.setRowCount(0);

        List<Order> ordini;
        if (isAdmin) {
            ordini = orderController.getAllOrdersWithUsername();
        } else if (utente != null) {
            ordini = orderController.getOrdersByUser(utente.getId());
        } else {
            ordini = null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        if (ordini != null) {
            for (Order o : ordini) {
                String dataOrdine = o.getOrderDate() != null ? o.getOrderDate().format(formatter) : "";
                double totale = o.getTotal() != null ? o.getTotal().doubleValue() : 0.0;
                if (isAdmin) {
                    modello.addRow(new Object[]{o.getId(), o.getUsername(), dataOrdine, totale});
                } else {
                    modello.addRow(new Object[]{o.getId(), dataOrdine, totale});
                }
            }
        }
        // Reset dettaglio ordine e messaggio
        DefaultTableModel modelloDett = (DefaultTableModel) tabellaDettaglio.getModel();
        modelloDett.setRowCount(0);
        labelDettaglio.setText("Seleziona un ordine per vedere i dettagli.");

        // Dopo aver caricato gli ordini, aggiorna il filtro se admin
        if (isAdmin && searchField != null) {
            filtroUsername();
        }
    }

    // Carica i dettagli di un ordine selezionato nella tabella dettaglio.
    private void caricaDettaglio(int orderId) {
        OrderController orderController = new OrderController();
        DefaultTableModel modelloDett = (DefaultTableModel) tabellaDettaglio.getModel();
        modelloDett.setRowCount(0);
        List<OrderItem> items = orderController.getOrderItems(orderId);
        if (items != null) {
            for (OrderItem it : items) {
                modelloDett.addRow(new Object[]{it.getProductName(), it.getQuantity(), it.getPrice().doubleValue()});
            }
        }
        labelDettaglio.setText("Dettagli ordine ID: " + orderId);
    }

    // Metodo pubblico per aggiornare lo storico ordini dall'esterno.
    public void refresh() {
        caricaOrdini();
    }

    // Metodo per filtrare la lista ordini per username (solo admin).
    private void filtroUsername() {
        if (!isAdmin || searchField == null) return;
        String testo = searchField.getText();
        if (testo.trim().length() == 0) {
            sorterOrdini.setRowFilter(null);
        } else {
            // Username è la colonna 1
            sorterOrdini.setRowFilter(RowFilter.regexFilter("(?i)" + testo, 1));
        }
    }
}