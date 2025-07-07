package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;

public class CartItemResponse {
    @SerializedName("product_id")
    private Product product;
    
    private int quantity;
    private double price;
    private double total;

    // Constructors
    public CartItemResponse() {}

    public CartItemResponse(Product product, int quantity, double price, double total) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    // Getters and Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    // Helper methods
    public String getProductId() {
        return product != null ? product.getId() : null;
    }
    
    public String getProductName() {
        return product != null ? product.getName() : "";
    }
    
    public String getProductImageUrl() {
        return product != null ? product.getPrimaryImageUrl() : null;
    }
} 