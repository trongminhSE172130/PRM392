package com.fpt.project.data.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String email;
    private String password;
    
    @SerializedName("full_name")  // API expects "full_name" not "fullName"
    private String fullName;
    
    private String phone;

    public RegisterRequest(String email, String password, String fullName, String phone) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
