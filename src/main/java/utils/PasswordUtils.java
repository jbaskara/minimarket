package utils;

import org.mindrot.jbcrypt.BCrypt;

// Utility per la gestione sicura delle password usando BCrypt.
public class PasswordUtils {
    // Esegue l'hashing di una password in chiaro.
    public static String hashPassword(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    // Verifica se una password in chiaro corrisponde all'hash memorizzato.
    public static boolean checkPassword(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }
}