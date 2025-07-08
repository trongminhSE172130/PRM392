package com.fpt.project.data.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessage implements Serializable {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("conversation_id")
    @JsonAdapter(ConversationIdDeserializer.class)
    private ConversationInfo conversationId;
    
    @SerializedName("sender_id")
    private Sender sender;
    
    @SerializedName("sender_type")
    private String senderType; // customer, store, system
    
    @SerializedName("sender_name")
    private String senderName;
    
    @SerializedName("content")
    private String message;
    
    @SerializedName("message_type")
    private String messageType; // text, image, system
    
    @SerializedName("is_read")
    private boolean isRead;
    
    @SerializedName("read_at")
    private String readAt;
    
    @SerializedName("edited_at")
    private String editedAt;
    
    @SerializedName("attachments")
    private List<String> attachments;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("__v")
    private int version;
    
    // Legacy fields for backward compatibility
    private String userId;  // Derived from sender
    private String senderId; // Derived from sender
    private Date timestamp; // Parsed from createdAt
    private String imageUrl; // From attachments

    // Constructors
    public ChatMessage() {
        parseTimestamp();
        deriveLegacyFields();
    }

    public ChatMessage(String userId, String message, String senderType) {
        this.userId = userId;
        this.senderId = userId;
        this.message = message;
        this.senderType = senderType;
        this.isRead = false;
        this.timestamp = new Date();
        this.messageType = "text";
    }
    
    // Constructor for API response
    public ChatMessage(String id, ConversationInfo conversationId, Sender sender, String senderType, 
                      String senderName, String content, String messageType, boolean isRead, 
                      String createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.sender = sender;
        this.senderType = senderType;
        this.senderName = senderName;
        this.message = content;
        this.messageType = messageType;
        this.isRead = isRead;
        this.createdAt = createdAt;
        parseTimestamp();
        deriveLegacyFields();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ConversationInfo getConversationId() { return conversationId; }
    public void setConversationId(ConversationInfo conversationId) { this.conversationId = conversationId; }
    
    // Helper method to get conversation ID as string for backward compatibility
    public String getConversationIdString() {
        return conversationId != null ? conversationId.getId() : null;
    }

    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { 
        this.sender = sender; 
        deriveLegacyFields();
    }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getReadAt() { return readAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }

    public String getEditedAt() { return editedAt; }
    public void setEditedAt(String editedAt) { this.editedAt = editedAt; }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { 
        this.attachments = attachments;
        extractImageUrl();
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { 
        this.createdAt = createdAt; 
        parseTimestamp();
    }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    // Legacy getters for backward compatibility
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Utility methods
    public boolean isFromCustomer() {
        return "customer".equals(senderType);
    }

    public boolean isFromStore() {
        return "store".equals(senderType);
    }

    public boolean isSystemMessage() {
        return "system".equals(senderType);
    }

    public boolean hasImage() {
        return imageUrl != null && !imageUrl.isEmpty();
    }

    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    public boolean isEdited() {
        return editedAt != null && !editedAt.isEmpty();
    }

    // Parse ISO timestamp from API
    private void parseTimestamp() {
        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                this.timestamp = sdf.parse(createdAt);
            } catch (ParseException e) {
                // Fallback to current time if parsing fails
                this.timestamp = new Date();
            }
        } else {
            this.timestamp = new Date();
        }
    }

    // Extract legacy fields from new API structure
    private void deriveLegacyFields() {
        if (sender != null) {
            this.userId = sender.getId();
            this.senderId = sender.getId();
        }
    }

    // Extract image URL from attachments
    private void extractImageUrl() {
        if (attachments != null && !attachments.isEmpty()) {
            // Find first image attachment
            for (String attachment : attachments) {
                if (attachment != null && (attachment.contains(".jpg") || 
                    attachment.contains(".jpeg") || attachment.contains(".png") || 
                    attachment.contains(".gif") || attachment.contains(".webp"))) {
                    this.imageUrl = attachment;
                    break;
                }
            }
        }
    }

    // Get sender email for display
    public String getSenderEmail() {
        return sender != null ? sender.getEmail() : "";
    }

    // Get formatted timestamp
    public String getFormattedTime() {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(timestamp);
        }
        return "";
    }

    // Get formatted date
    public String getFormattedDate() {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(timestamp);
        }
        return "";
    }
}
