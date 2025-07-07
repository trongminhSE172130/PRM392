package com.fpt.project.data.model.request;

import com.google.gson.annotations.SerializedName;

public class AddToCartRequest {
    @SerializedName("product_id")
    private String productId;
    
    private int quantity;

    // Constructors
    public AddToCartRequest() {}

    public AddToCartRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

} 