package com.fpt.project.data.model.response;

import com.fpt.project.data.model.Order;

public class CheckoutResponse {
    private boolean success;
    private Order data;
    private String message;

    public CheckoutResponse() {}

    public CheckoutResponse(boolean success, Order data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Order getData() { return data; }
    public void setData(Order data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 