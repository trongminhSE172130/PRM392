package com.fpt.project.data.model.response;

import com.fpt.project.data.model.ChatMessage;
import com.google.gson.annotations.SerializedName;

public class SendMessageResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private ChatMessage data; // The created message
    
    // Constructors
    public SendMessageResponse() {}
    
    public SendMessageResponse(boolean success, String message, ChatMessage data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public ChatMessage getData() {
        return data;
    }
    
    public void setData(ChatMessage data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "SendMessageResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
} 