package com.fpt.project.data.model;

import android.util.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;

public class ConversationIdDeserializer implements JsonDeserializer<ConversationInfo> {
    
    private static final String TAG = "ConversationIdDeserializer";
    
    @Override
    public ConversationInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            // Handle string format: "conversation_id": "675d8f8a2e5bc72f8c4a1234"
            String conversationId = json.getAsString();
            Log.d(TAG, "Deserializing conversation_id as STRING: " + conversationId);
            ConversationInfo conversationInfo = new ConversationInfo();
            conversationInfo.setId(conversationId);
            return conversationInfo;
            
        } else if (json.isJsonObject()) {
            // Handle object format: "conversation_id": { "_id": "...", "subject": "...", ... }
            Log.d(TAG, "Deserializing conversation_id as OBJECT");
            JsonObject jsonObject = json.getAsJsonObject();
            Log.d(TAG, "Object keys: " + jsonObject.keySet().toString());
            
            // Manually parse JsonObject to avoid recursion
            ConversationInfo conversationInfo = new ConversationInfo();
            
            // Parse basic fields that exist in ConversationInfo class
            if (jsonObject.has("_id") && !jsonObject.get("_id").isJsonNull()) {
                conversationInfo.setId(jsonObject.get("_id").getAsString());
            }
            if (jsonObject.has("customer_id") && !jsonObject.get("customer_id").isJsonNull()) {
                conversationInfo.setCustomerId(jsonObject.get("customer_id").getAsString());
            }
            if (jsonObject.has("session_id") && !jsonObject.get("session_id").isJsonNull()) {
                conversationInfo.setSessionId(jsonObject.get("session_id").getAsString());
            }
            if (jsonObject.has("customer_name") && !jsonObject.get("customer_name").isJsonNull()) {
                conversationInfo.setCustomerName(jsonObject.get("customer_name").getAsString());
            }
            if (jsonObject.has("customer_email") && !jsonObject.get("customer_email").isJsonNull()) {
                conversationInfo.setCustomerEmail(jsonObject.get("customer_email").getAsString());
            }
            if (jsonObject.has("subject") && !jsonObject.get("subject").isJsonNull()) {
                conversationInfo.setSubject(jsonObject.get("subject").getAsString());
            }
            if (jsonObject.has("status") && !jsonObject.get("status").isJsonNull()) {
                conversationInfo.setStatus(jsonObject.get("status").getAsString());
            }
            if (jsonObject.has("assigned_to") && !jsonObject.get("assigned_to").isJsonNull()) {
                conversationInfo.setAssignedTo(jsonObject.get("assigned_to").getAsString());
            }
            if (jsonObject.has("priority") && !jsonObject.get("priority").isJsonNull()) {
                conversationInfo.setPriority(jsonObject.get("priority").getAsString());
            }
            if (jsonObject.has("__v") && !jsonObject.get("__v").isJsonNull()) {
                conversationInfo.setVersion(jsonObject.get("__v").getAsInt());
            }
            
            // Parse date fields as strings (not Date objects)
            if (jsonObject.has("created_at") && !jsonObject.get("created_at").isJsonNull()) {
                conversationInfo.setCreatedAt(jsonObject.get("created_at").getAsString());
            }
            if (jsonObject.has("updated_at") && !jsonObject.get("updated_at").isJsonNull()) {
                conversationInfo.setUpdatedAt(jsonObject.get("updated_at").getAsString());
            }
            
            // Parse tags array
            if (jsonObject.has("tags") && !jsonObject.get("tags").isJsonNull()) {
                try {
                    @SuppressWarnings("unchecked")
                    List<String> tags = context.deserialize(jsonObject.get("tags"), List.class);
                    conversationInfo.setTags(tags);
                } catch (Exception e) {
                    Log.w(TAG, "Failed to parse tags", e);
                }
            }
            
            // Parse nested objects using context (these won't cause recursion since they're different types)
            if (jsonObject.has("last_message") && !jsonObject.get("last_message").isJsonNull()) {
                try {
                    ConversationInfo.LastMessage lastMessage = context.deserialize(jsonObject.get("last_message"), ConversationInfo.LastMessage.class);
                    conversationInfo.setLastMessage(lastMessage);
                } catch (Exception e) {
                    Log.w(TAG, "Failed to parse last_message", e);
                }
            }
            if (jsonObject.has("unread_count") && !jsonObject.get("unread_count").isJsonNull()) {
                try {
                    ConversationInfo.UnreadCount unreadCount = context.deserialize(jsonObject.get("unread_count"), ConversationInfo.UnreadCount.class);
                    conversationInfo.setUnreadCount(unreadCount);
                } catch (Exception e) {
                    Log.w(TAG, "Failed to parse unread_count", e);
                }
            }
            
            Log.d(TAG, "Manually parsed ConversationInfo: " + conversationInfo.toString());
            return conversationInfo;
            
        } else {
            // Handle null or other cases
            Log.d(TAG, "Deserializing conversation_id as NULL or UNKNOWN type");
            return null;
        }
    }
} 