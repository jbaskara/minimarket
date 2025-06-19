package controller;

import dao.UserDAO;
import model.User;
import utils.PasswordUtils;

public class AuthController {
    private UserDAO userDAO = new UserDAO();	// DAO per la gestione degli utenti

    // Effettua il login dell'utente verificando email e password.
    public User login(String email, String password) {
        try {
            User user = userDAO.findByEmail(email);	 // Cerca utente per email
            // Controlla se l'utente esiste e se la password è corretta
            if (user != null && PasswordUtils.checkPassword(password, user.getPassword())) {
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();	// Gestione di eventuali errori
        }
        return null;
    }

    // Registra un nuovo utente nel sistema se l'email non è già presente.
    public boolean register(User user) {
        try {
            // Verifica se l'email è già registrata
            if (userDAO.findByEmail(user.getEmail()) != null) {
                return false;
            }
            user.setPassword(PasswordUtils.hashPassword(user.getPassword()));	// Cripta la password prima di salvarla
            userDAO.save(user);	// Salva il nuovo utente nel database
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}