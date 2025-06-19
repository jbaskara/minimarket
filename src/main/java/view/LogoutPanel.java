package view;

import javax.swing.*;
import java.awt.*;

/**
 * Pannello per la gestione del logout dell'utente.
 * Mostra un messaggio e un pulsante per effettuare il logout e tornare alla schermata di login.
 */
@SuppressWarnings("serial")
public class LogoutPanel extends JPanel {
    // Costruttore che crea il pannello di logout.
    @SuppressWarnings("unused")
	public LogoutPanel(JFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createRigidArea(new Dimension(0, 60)));

        JLabel label = new JLabel("Premi il bottone per uscire", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setPreferredSize(new Dimension(120, 32));
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new LoginPanel().setVisible(true);
        });
        add(logoutBtn);
    }
}