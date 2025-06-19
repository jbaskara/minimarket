package db;

import java.sql.Connection;
import java.sql.DriverManager;

// Gestisce la connessione al database PostgreSQL.
public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/minimarket";
    private static final String USER = "";	// Da impostare, "postgres" come predefinito
    private static final String PASSWORD = "";	// Da impostare

    // Restituisce una nuova connessione al database.
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}