package com.fpt.project.data.model.response;

import com.fpt.project.data.model.User;

public class RegisterResponse {
    private boolean success;
    private String message;
    private RegisterData data;

    // Inner class for nested data structure
    public static class RegisterData {
        private User user;
        private String token;

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    // Constructors
    public RegisterResponse() {}

    public RegisterResponse(boolean success, String message, RegisterData data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public RegisterData getData() { return data; }
    public void setData(RegisterData data) { this.data = data; }
    
    // Convenience methods for backward compatibility
    public User getUser() { 
        return data != null ? data.getUser() : null; 
    }

    public String getToken() { 
        return data != null ? data.getToken() : null; 
    }
} 