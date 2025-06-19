package dao;

import db.DBConnection;
import model.Order;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Restituisce tutti gli ordini effettuati da uno specifico utente.
    public List<Order> findByUserId(int userId) throws Exception {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id=? ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        }
        return orders;
    }

    // Restituisce tutti gli ordini presenti nel database, ordinati per data decrescente.
    public List<Order> findAll() throws Exception {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        }
        return orders;
    }

    // Restituisce tutti gli ordini, includendo anche il nome utente associato a ciascun ordine.
    public List<Order> findAllWithUsername() throws Exception {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username " +
                     "FROM orders o JOIN users u ON o.user_id = u.id " +
                     "ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapOrderWithUsername(rs));
            }
        }
        return orders;
    }

    // Restituisce tutti gli articoli associati a un ordine specifico.
    public List<OrderItem> findItemsByOrderId(int orderId) throws Exception {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.id, p.name, oi.quantity, oi.price " +
                     "FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setProductName(rs.getString("name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));
                items.add(item);
            }
        }
        return items;
    }

    // Inserisce un nuovo ordine e i relativi articoli nel database tramite una transazione.
    public boolean addOrder(Order order, List<OrderItem> items) throws Exception {
        String insertOrderSql = "INSERT INTO orders (user_id, order_date, total) VALUES (?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        ResultSet generatedKeys = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Inizio della transazione

            // Inserimento dell'ordine
            psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getUserId());
            psOrder.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            psOrder.setBigDecimal(3, order.getTotal());
            int affectedRows = psOrder.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Recupero dell'ID generato per l'ordine
            generatedKeys = psOrder.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // Inserimento degli articoli dell'ordine
            psItem = conn.prepareStatement(insertItemSql);
            for (OrderItem item : items) {
                psItem.setInt(1, orderId);
                psItem.setInt(2, item.getProductId());
                psItem.setInt(3, item.getQuantity());
                psItem.setBigDecimal(4, item.getPrice());
                psItem.addBatch();
            }
            psItem.executeBatch();

            conn.commit(); // Fine della transazione
            return true;
        } catch (Exception e) {
            if (conn != null) conn.rollback(); // Rollback in caso di errore
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (psOrder != null) psOrder.close();
            if (psItem != null) psItem.close();
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) conn.close();
        }
    }

    // Mappa un ResultSet su un oggetto Order.
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        o.setTotal(rs.getBigDecimal("total"));
        return o;
    }

    // Mappa un ResultSet su un oggetto Order, includendo anche l'username.
    private Order mapOrderWithUsername(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        o.setTotal(rs.getBigDecimal("total"));
        o.setUsername(rs.getString("username"));
        return o;
    }
}