package controller;

import dao.CartDAO;
import dao.ProductDAO;
import model.CartItem;
import model.Product;

import java.util.List;

public class CartController {
    private CartDAO cartDAO = new CartDAO();	// DAO per la gestione del carrello
    private ProductDAO productDAO = new ProductDAO();	 // DAO per la gestione dei prodotti

    // Restituisce tutti gli articoli presenti nel carrello di un utente.
    public List<CartItem> getCartItems(int userId) {
        try {
            return cartDAO.findByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();	// Gestione di eventuali errori
        }
        return null;
    }

    // Aggiunge un prodotto al carrello di un utente, se disponibile in magazzino.
    public boolean addToCart(int userId, int productId, int quantity) {
        try {
            Product product = productDAO.findById(productId);	// Cerca il prodotto e verifica la disponibilità in magazzino
            if (product != null && product.getQuantity() >= quantity) {
                cartDAO.addToCart(userId, productId, quantity);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Rimuove un articolo dal carrello tramite l'ID dell'articolo.
    public boolean removeFromCart(int cartItemId) {
        try {
            cartDAO.remove(cartItemId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Calcola il totale del carrello per un utente.
    public double getCartTotal(int userId) {
        try {
            return cartDAO.calculateTotal(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Procede all'acquisto di tutti gli articoli presenti nel carrello dell'utente.
    public boolean purchaseCart(int userId) {
        try {
            cartDAO.purchaseCart(userId);	// chiama la stored procedure per l'acquisto
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}