package dao;

import db.DBConnection;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@SuppressWarnings("unused")
public class ProductDAO {

    // Restituisce tutti i prodotti presenti nel database, ordinati per nome.
    public List<Product> findAll() throws Exception {
        List<Product> prodotti = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prodotti.add(mapProduct(rs));
            }
        }
        return prodotti;
    }

    // Cerca un prodotto dato il suo ID.
    public Product findById(int id) throws Exception {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapProduct(rs);
            }
        }
        return null;
    }

    // Salva un nuovo prodotto nel database.
    public void save(Product product) throws Exception {
        String sql = "INSERT INTO products (name, quantity, price, category) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getCategory());
            ps.executeUpdate();
        }
    }

    // Aggiorna i dati di un prodotto esistente.
    public void update(Product product) throws Exception {
        String sql = "UPDATE products SET name=?, quantity=?, price=?, category=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getCategory());
            ps.setInt(5, product.getId());
            ps.executeUpdate();
        }
    }

    // Elimina un prodotto dal database tramite il suo ID.
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Filtra i prodotti in base a nome, categoria e fascia di prezzo.
    public List<Product> cercaProdotti(String nome, String categoria, double prezzoMin, double prezzoMax) throws Exception {
        List<Product> prodotti = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Filtra per nome se specificato
        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND LOWER(name) LIKE ?");
            params.add("%" + nome.trim().toLowerCase() + "%");
        }
        // Filtra per categoria se specificata e diversa da "Tutte"
        if (categoria != null && !categoria.equalsIgnoreCase("Tutte")) {
            sql.append(" AND category = ?");
            params.add(categoria);
        }
        // Filtra in base al prezzo minimo e massimo
        sql.append(" AND price >= ? AND price <= ?");
        params.add(prezzoMin);
        params.add(prezzoMax);

        sql.append(" ORDER BY name");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(mapProduct(rs));
                }
            }
        }
        return prodotti;
    }

    // Mappa un ResultSet su un oggetto Product.
    private Product mapProduct(ResultSet rs) throws SQLException {
        // Usa il costruttore completo per valorizzare i campi
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("quantity"),
            rs.getBigDecimal("price"),
            rs.getString("category")
        );
    }
}