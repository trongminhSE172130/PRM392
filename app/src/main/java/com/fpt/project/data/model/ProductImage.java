package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ProductImage implements Serializable {
    @SerializedName("_id")
    private String id;
    
    private String url;
    
    @SerializedName("is_primary")
    private boolean isPrimary;

    // Constructors
    public ProductImage() {}

    public ProductImage(String url, boolean isPrimary) {
        this.url = url;
        this.isPrimary = isPrimary;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
} 