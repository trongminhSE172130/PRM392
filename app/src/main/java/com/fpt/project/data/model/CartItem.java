package com.fpt.project.data.model;

public class CartItem {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private String createdAt;
    private Product product; // For displaying product details in cart

    // Constructors
    public CartItem() {}

    public CartItem(int userId, int productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

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
