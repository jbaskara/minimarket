package controller;

import dao.OrderDAO;
import model.Order;
import model.OrderItem;

import java.util.List;

public class OrderController {
    private OrderDAO orderDAO = new OrderDAO();		// DAO per la gestione degli ordini

    // Restituisce tutti gli ordini effettuati da un utente specifico.
    public List<Order> getOrdersByUser(int userId) {
        try {
            return orderDAO.findByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();	// Gestione di eventuali errori
        }
        return null;
    }

    // Restituisce tutti gli ordini presenti nel sistema.
    public List<Order> getAllOrders() {
        try {
            return orderDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Restituisce tutti gli ordini con il nome utente associato ad ogni ordine.
    public List<Order> getAllOrdersWithUsername() {
        try {
            return orderDAO.findAllWithUsername();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Restituisce tutti gli articoli di un ordine specifico.
    public List<OrderItem> getOrderItems(int orderId) {
        try {
            return orderDAO.findItemsByOrderId(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Aggiunge un nuovo ordine e i relativi articoli al database.
    public boolean addOrder(Order order, List<OrderItem> items) {
        try {
            return orderDAO.addOrder(order, items);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}