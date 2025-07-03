package com.fpt.project.data.model;

public class ChatMessage {
    private int id;
    private String userId;  // Changed to String to match User.id
    private String message;
    private String senderType; // customer, store
    private boolean isRead;
    private String createdAt;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(String userId, String message, String senderType) {  // userId now String
        this.userId = userId;
        this.message = message;
        this.senderType = senderType;
        this.isRead = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }  // Return String
    public void setUserId(String userId) { this.userId = userId; }  // Parameter String

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Utility methods
    public boolean isFromCustomer() {
        return "customer".equals(senderType);
    }

    public boolean isFromStore() {
        return "store".equals(senderType);
    }
}
