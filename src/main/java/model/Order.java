package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Rappresenta un ordine effettuato da un utente.
public class Order {
    private int id;
    private int userId;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private String username;           // visualizzato solo da admin

    // Costruttore vuoto.
    public Order() {}

    // Costruttore completo senza username (per clienti).
    public Order(int id, int userId, LocalDateTime orderDate, BigDecimal total) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.total = total;
    }

    // Costruttore con username (per admin).
    public Order(int id, int userId, LocalDateTime orderDate, BigDecimal total, String username) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.total = total;
        this.username = username;
    }

    // Getters & Setters

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getOrderDate() { return orderDate; }

    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotal() { return total; }

    public void setTotal(BigDecimal total) { this.total = total; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}