package view;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principale per l'avvio dell'applicazione Mini Market.
 * Inizializza il Look and Feel di sistema e apre la finestra di login.
 */
public class Main {
    public static void main(String[] args) {
        // Imposta il Look and Feel di sistema per migliorare l'estetica
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Avvia la finestra di login in modo thread-safe
        SwingUtilities.invokeLater(() -> {
            new LoginPanel().setVisible(true);
        });
    }
}