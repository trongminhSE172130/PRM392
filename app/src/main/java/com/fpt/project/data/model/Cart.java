package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Cart {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("user_id")
    private String userId;
    
    private List<CartItemResponse> items;
    
    @SerializedName("total_items")
    private int totalItems;
    
    @SerializedName("total_amount")
    private double totalAmount;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("__v")
    private int version;

    // Constructors
    public Cart() {}

    public Cart(String userId, List<CartItemResponse> items, int totalItems, double totalAmount) {
        this.userId = userId;
        this.items = items;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    
    // Helper methods
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
    
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    
    public double calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        
        double total = 0.0;
        for (CartItemResponse item : items) {
            total += item.getTotal();
        }
        return total;
    }
} 