package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ConversationInfo implements Serializable {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("customer_id")
    private String customerId;
    
    @SerializedName("session_id")
    private String sessionId;
    
    @SerializedName("customer_name")
    private String customerName;
    
    @SerializedName("customer_email")
    private String customerEmail;
    
    @SerializedName("subject")
    private String subject;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("assigned_to")
    private String assignedTo;
    
    @SerializedName("tags")
    private List<String> tags;
    
    @SerializedName("priority")
    private String priority;
    
    @SerializedName("last_message")
    private LastMessage lastMessage;
    
    @SerializedName("unread_count")
    private UnreadCount unreadCount;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("__v")
    private int version;
    
    // Nested classes for complex fields
    public static class LastMessage implements Serializable {
        @SerializedName("content")
        private String content;
        
        @SerializedName("sender_type")
        private String senderType;
        
        @SerializedName("timestamp")
        private String timestamp;
        
        // Constructors
        public LastMessage() {}
        
        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
    
    public static class UnreadCount implements Serializable {
        @SerializedName("customer")
        private int customer;
        
        @SerializedName("store")
        private int store;
        
        // Constructors
        public UnreadCount() {}
        
        // Getters and Setters
        public int getCustomer() { return customer; }
        public void setCustomer(int customer) { this.customer = customer; }
        
        public int getStore() { return store; }
        public void setStore(int store) { this.store = store; }
    }
    
    // Constructors
    public ConversationInfo() {}
    
    public ConversationInfo(String id, String subject, String status) {
        this.id = id;
        this.subject = subject;
        this.status = status;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public LastMessage getLastMessage() {
        return lastMessage;
    }
    
    public void setLastMessage(LastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public UnreadCount getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(UnreadCount unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return "ConversationInfo{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", subject='" + subject + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
} 