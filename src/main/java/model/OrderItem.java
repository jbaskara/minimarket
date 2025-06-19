package model;

import java.math.BigDecimal;

// Rappresenta un articolo di un ordine.
public class OrderItem {
    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    // Costruttore vuoto.
    public OrderItem() {}

    // Costruttore completo per inizializzare tutti i campi.
    public OrderItem(int id, int productId, String productName, int quantity, BigDecimal price) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}