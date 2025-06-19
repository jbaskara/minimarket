package controller;

import dao.ProductDAO;
import model.Product;

import java.util.List;

public class ProductController {
    private ProductDAO productDAO = new ProductDAO();	    // DAO per la gestione dei prodotti

    // Restituisce la lista di tutti i prodotti presenti nel database.
    public List<Product> getAllProducts() {
        try {
            return productDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();	// Gestione di eventuali errori
        }
        return null;
    }

    // Aggiunge un nuovo prodotto al database.
    public boolean addProduct(Product product) {
        try {
            productDAO.save(product);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Aggiorna le informazioni di un prodotto esistente.
    public boolean updateProduct(Product product) {
        try {
            productDAO.update(product);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Elimina un prodotto dal database tramite il suo ID.
    public boolean deleteProduct(int productId) {
        try {
            productDAO.delete(productId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Filtra i prodotti in base a nome, categoria e fascia di prezzo.
    public List<Product> cercaProdotti(String nome, String categoria, double prezzoMin, double prezzoMax) {
        try {
            return productDAO.cercaProdotti(nome, categoria, prezzoMin, prezzoMax);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}