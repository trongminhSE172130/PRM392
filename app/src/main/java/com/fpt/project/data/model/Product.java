package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    @SerializedName("_id")
    private String id;
    
    private String name;
    private String description;
    private double price;
    
    @SerializedName("category_id")
    private Category category;
    
    private List<ProductImage> images;
    
    @SerializedName("stock_quantity")
    private int stockQuantity;
    
    private String sku;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("is_featured")
    private boolean isFeatured;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("__v")
    private int version;

    // Constructors
    public Product() {}

    public Product(String name, String description, double price, int stockQuantity, 
                   Category category, String sku) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.sku = sku;
        this.isActive = true;
        this.isFeatured = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    
    // Helper method to get primary image URL
    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            for (ProductImage image : images) {
                if (image.isPrimary()) {
                    return image.getUrl();
                }
            }
            // If no primary image, return first image
            return images.get(0).getUrl();
        }
        return null;
    }
    
    // Helper method to get category name
    public String getCategoryName() {
        return category != null ? category.getName() : "";
    }
}
