package com.fpt.project.data.model;

import java.util.List;

public class Order {
    private int id;
    private String userId;  // Changed to String to match User.id
    private double totalAmount;
    private String status; // pending, confirmed, shipped, delivered, cancelled
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus; // pending, paid, failed
    private String createdAt;
    private String updatedAt;
    private List<OrderItem> orderItems;

    // Constructors
    public Order() {}

    public Order(String userId, double totalAmount, String shippingAddress, String paymentMethod) {  // userId now String
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.status = "pending";
        this.paymentStatus = "pending";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }  // Return String
    public void setUserId(String userId) { this.userId = userId; }  // Parameter String

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
