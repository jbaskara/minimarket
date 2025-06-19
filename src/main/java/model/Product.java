package model;

import java.math.BigDecimal;

// Rappresenta un prodotto nel sistema.
public class Product {
    private int id;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String category;

    // Costruttore vuoto.
    public Product() {}

    // Costruttore completo per Product.
    public Product(int id, String name, int quantity, BigDecimal price, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}