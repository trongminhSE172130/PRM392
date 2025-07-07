package com.fpt.project.data.model.response;

import com.fpt.project.data.model.Cart;

public class CartResponse {
    private boolean success;
    private Cart data;
    private String message;

    // Constructors
    public CartResponse() {}

    public CartResponse(boolean success, Cart data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Cart getData() { return data; }
    public void setData(Cart data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 