package com.fpt.project.data.model;

public class CartItem {
    private int id;
    private String userId;  // Changed to String to match User.id
    private String productId;
    private int quantity;
    private String createdAt;
    private Product product; // For displaying product details in cart

    // Constructors
    public CartItem() {}

    public CartItem(String userId, String productId, int quantity) {  // userId now String
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }  // Return String
    public void setUserId(String userId) { this.userId = userId; }  // Parameter String

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    // Utility method to calculate total price for this cart item
    public double getTotalPrice() {
        return product != null ? product.getPrice() * quantity : 0;
    }
}
