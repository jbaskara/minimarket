package model;

import java.math.BigDecimal;

// Rappresenta un articolo presente nel carrello di un utente.
public class CartItem {
    private int id;
    private String productName;
    private int quantity;
    private BigDecimal price;

    // Costruttore vuoto.
    public CartItem() {}

    // Costruttore completo per creare un CartItem con tutti i campi valorizzati.
    public CartItem(int id, String productName, int quantity, BigDecimal price) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters & Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }
}