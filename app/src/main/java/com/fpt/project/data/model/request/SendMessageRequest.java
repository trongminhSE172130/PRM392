package com.fpt.project.data.model.request;

import com.google.gson.annotations.SerializedName;

public class SendMessageRequest {
    @SerializedName("content")
    private String content;
    
    // Constructors
    public SendMessageRequest() {}
    
    public SendMessageRequest(String content) {
        this.content = content;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "SendMessageRequest{" +
                "content='" + content + '\'' +
                '}';
    }
} 