package com.fpt.project.data.model;

import java.io.Serializable;
import java.util.Date;

public class ChatConversation implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String userAvatarUrl;
    private String lastMessage;
    private Date lastMessageTime;
    private int unreadCount;
    private boolean isOnline;
    private String chatType; // "support", "order", "general"
    private boolean isPriority;
    private String status; // "active", "closed", "waiting"

    // Constructors
    public ChatConversation() {}

    public ChatConversation(String id, String userId, String userName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.lastMessageTime = new Date();
        this.unreadCount = 0;
        this.isOnline = false;
        this.chatType = "general";
        this.isPriority = false;
        this.status = "active";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper methods
    public String getFormattedTime() {
        if (lastMessageTime == null) return "";
        
        Date now = new Date();
        long diff = now.getTime() - lastMessageTime.getTime();
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffMinutes < 1) {
            return "Now";
        } else if (diffMinutes < 60) {
            return diffMinutes + "m";
        } else if (diffHours < 24) {
            return diffHours + "h";
        } else if (diffDays == 1) {
            return "Yesterday";
        } else if (diffDays < 7) {
            return diffDays + "d";
        } else {
            return android.text.format.DateFormat.format("MMM dd", lastMessageTime).toString();
        }
    }

    public boolean hasUnreadMessages() {
        return unreadCount > 0;
    }

    public String getChatTypeDisplayName() {
        switch (chatType.toLowerCase()) {
            case "support":
                return "Customer Support";
            case "order":
                return "Order Support";
            case "general":
            default:
                return "General Chat";
        }
    }
} 