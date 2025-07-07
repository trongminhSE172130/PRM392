package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    private String id;  // Changed to String to match API response
    private String username;
    private String email;
    private String fullName;
    @SerializedName("full_name")
    private String full_name;  // API returns "full_name" 
    private String phone;
    private String address;
    private String role;  // Added role field from API
    private String createdAt;

    // Constructors
    public User() {}

    public User(String username, String email, String fullName, String phone, String address) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { 
        // Prioritize full_name from API, fallback to fullName
        return full_name != null ? full_name : fullName; 
    }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
