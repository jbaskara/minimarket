package dao;

import db.DBConnection;
import model.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // Restituisce tutti gli articoli nel carrello di un utente.
    public List<CartItem> findByUserId(int userId) throws Exception {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT c.id, p.name, c.quantity, p.price " +
                     "FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setId(rs.getInt("id"));
                item.setProductName(rs.getString("name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));
                items.add(item);
            }
        }
        return items;
    }

    // Aggiunge un prodotto al carrello di un utente.
    public void addToCart(int userId, int productId, int quantity) throws Exception {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }

    // Rimuove un articolo dal carrello tramite il suo ID.
    public void remove(int cartItemId) throws Exception {
        String sql = "DELETE FROM cart WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            ps.executeUpdate();
        }
    }

    // Calcola il totale dell'importo del carrello per un utente.
    public double calculateTotal(int userId) throws Exception {
        String sql = "SELECT SUM(c.quantity * p.price) AS total " +
                     "FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Esegue la procedura di acquisto del carrello per l'utente.
     * Chiama la stored procedure 'acquista_prodotti' nel database.
     */
    public void purchaseCart(int userId) throws Exception {
        String sql = "CALL acquista_prodotti(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.execute();
        }
    }
}