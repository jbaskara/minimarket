package dao;

import db.DBConnection;
import model.User;
import java.sql.*;

public class UserDAO {

    // Cerca un utente nel database tramite l'email.
    public User findByEmail(String email) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    // Salva un nuovo utente nel database.
    public void save(User user) throws Exception {
        String sql = "INSERT INTO users (username, nome, cognome, email, password, is_admin) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getNome());
            ps.setString(3, user.getCognome());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setBoolean(6, user.isAdmin());
            ps.executeUpdate();
        }
    }

    // Mappa un ResultSet su un oggetto User.
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setAdmin(rs.getBoolean("is_admin"));
        return u;
    }
}