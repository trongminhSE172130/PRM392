package com.fpt.project.data.model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double price;
    private String createdAt;
    private Product product; // For displaying product details

    // Constructors
    public OrderItem() {}

    public OrderItem(int orderId, int productId, int quantity, double price) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    // Utility method to calculate total price for this order item
    public double getTotalPrice() {
        return price * quantity;
    }
}
