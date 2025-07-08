package com.fpt.project.data.model.response;

import com.fpt.project.data.model.ChatMessage;
import com.fpt.project.data.model.Pagination;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MessagesResponse {
    @SerializedName("messages")
    private List<ChatMessage> messages;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    // Constructors
    public MessagesResponse() {}
    
    public MessagesResponse(List<ChatMessage> messages, Pagination pagination) {
        this.messages = messages;
        this.pagination = pagination;
    }
    
    // Getters and Setters
    public List<ChatMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    // Utility methods
    public boolean hasMessages() {
        return messages != null && !messages.isEmpty();
    }
    
    public int getMessageCount() {
        return messages != null ? messages.size() : 0;
    }
    
    public boolean canLoadMore() {
        return pagination != null && pagination.canLoadMore();
    }
    
    public int getTotalMessages() {
        return pagination != null ? pagination.getTotalMessages() : getMessageCount();
    }
    
    @Override
    public String toString() {
        return "MessagesResponse{" +
                "messageCount=" + getMessageCount() +
                ", pagination=" + pagination +
                '}';
    }
} 