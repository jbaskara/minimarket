package db;

import org.junit.jupiter.api.Test;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di unità per la classe DBConnection.
 * Verifica che la connessione al database venga creata correttamente e sia aperta.
 */
class DBConnectionTest {

    // Testa che il metodo getConnection() restituisca una connessione valida e aperta.
    @Test
    void testGetConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "La connessione non deve essere null");
            assertFalse(conn.isClosed(), "La connessione deve essere aperta");
        } catch (Exception e) {
            fail("Eccezione durante la connessione al database: " + e.getMessage());
        }
    }
}