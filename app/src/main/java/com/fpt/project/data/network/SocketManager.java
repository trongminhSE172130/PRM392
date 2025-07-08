package com.fpt.project.data.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fpt.project.data.model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {
    private static final String TAG = "SocketManager";
    private static SocketManager instance;
    private Socket socket;
    private Context context;
    private boolean isConnected = false;
    private String currentConversationId;
    
    // Listeners
    private List<OnMessageListener> messageListeners = new ArrayList<>();
    private List<OnTypingListener> typingListeners = new ArrayList<>();
    private List<OnConnectionListener> connectionListeners = new ArrayList<>();
    
    // Socket events
    private static final String EVENT_CONNECT = Socket.EVENT_CONNECT;
    private static final String EVENT_DISCONNECT = Socket.EVENT_DISCONNECT;
    private static final String EVENT_CONNECT_ERROR = Socket.EVENT_CONNECT_ERROR;
    
    // Custom events from backend
    private static final String EVENT_JOIN_CONVERSATION = "join_conversation";
    private static final String EVENT_LEAVE_CONVERSATION = "leave_conversation";
    private static final String EVENT_SEND_MESSAGE = "send_message";
    private static final String EVENT_START_CONVERSATION = "start_conversation";
    private static final String EVENT_TYPING = "typing";
    
    // Server response events
    private static final String EVENT_NEW_MESSAGE = "new_message";
    private static final String EVENT_USER_TYPING = "user_typing";
    private static final String EVENT_JOINED_CONVERSATION = "joined_conversation";
    private static final String EVENT_CONVERSATION_CREATED = "conversation_created";
    private static final String EVENT_MESSAGE_SENT = "message_sent";
    private static final String EVENT_MESSAGES_READ = "messages_read";
    private static final String EVENT_ERROR = "error";

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot initialize socket");
            return;
        }
        
        this.context = context.getApplicationContext();
        
        try {
            // Disconnect existing socket if any
            if (socket != null) {
                socket.disconnect();
                socket = null;
            }
            
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.timeout = 10000;
            options.reconnectionDelay = 1000;
            options.reconnectionAttempts = 5;
            
            // Initialize auth map if it's null
            if (options.auth == null) {
                options.auth = new HashMap<>();
                Log.d(TAG, "Initialized auth map for Socket.IO options");
            }
            
            // Check authentication - use token if logged in, otherwise use session ID
            SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
            String token = getAuthToken();
            
            if (isLoggedIn && token != null && !token.isEmpty()) {
                options.auth.put("token", token);
                Log.d(TAG, "Connecting with auth token (user logged in)");
            } else {
                String sessionId = getOrCreateSessionId();
                options.auth.put("sessionId", sessionId);
                Log.d(TAG, "Connecting with session ID (anonymous): " + sessionId);
            }
            
            // Validate URL before creating socket
            String url = ApiConfig.WEBSOCKET_URL;
            if (url == null || url.trim().isEmpty()) {
                Log.e(TAG, "WebSocket URL is null or empty");
                return;
            }
            
            Log.d(TAG, "Creating socket with URL: " + url);
            socket = IO.socket(url, options);
            
            if (socket == null) {
                Log.e(TAG, "Failed to create socket - IO.socket returned null");
                return;
            }
            
            setupEventListeners();
            Log.d(TAG, "Socket initialized successfully");
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid WebSocket URL: " + ApiConfig.WEBSOCKET_URL, e);
            socket = null;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during socket initialization", e);
            socket = null;
        }
    }

    private void setupEventListeners() {
        if (socket == null) {
            Log.e(TAG, "Cannot setup event listeners - socket is null");
            return;
        }
        
        Log.d(TAG, "Setting up socket event listeners");
        socket.on(EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isConnected = true;
                Log.d(TAG, "Socket connected");
                notifyConnectionChanged(true);
            }
        });

        socket.on(EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isConnected = false;
                Log.d(TAG, "Socket disconnected");
                notifyConnectionChanged(false);
            }
        });

        socket.on(EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Socket connection error: " + args[0]);
                notifyConnectionChanged(false);
            }
        });

        socket.on(EVENT_NEW_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject messageJson = data.getJSONObject("message");
                    String conversationId = data.getString("conversationId");
                    
                    ChatMessage message = parseMessage(messageJson);
                    notifyNewMessage(message, conversationId);
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing new message", e);
                }
            }
        });

        socket.on(EVENT_USER_TYPING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String userId = data.getString("userId");
                    String userName = data.getString("userName");
                    boolean isTyping = data.getBoolean("isTyping");
                    String conversationId = data.getString("conversationId");
                    
                    notifyTypingChanged(userId, userName, isTyping, conversationId);
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing typing event", e);
                }
            }
        });

        socket.on(EVENT_JOINED_CONVERSATION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String conversationId = data.getString("conversationId");
                    boolean success = data.getBoolean("success");
                    
                    if (success) {
                        currentConversationId = conversationId;
                        Log.d(TAG, "Successfully joined conversation: " + conversationId);
                    }
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing joined conversation", e);
                }
            }
        });

        socket.on(EVENT_CONVERSATION_CREATED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject conversation = data.getJSONObject("conversation");
                    boolean success = data.getBoolean("success");
                    
                    if (success) {
                        String conversationId = conversation.getString("_id");
                        Log.d(TAG, "Conversation created: " + conversationId);
                        // Auto-join the new conversation
                        joinConversation(conversationId);
                    }
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing conversation created", e);
                }
            }
        });

        socket.on(EVENT_MESSAGE_SENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String messageId = data.getString("messageId");
                    boolean success = data.getBoolean("success");
                    
                    Log.d(TAG, "Message sent confirmation: " + success + " ID: " + messageId);
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing message sent", e);
                }
            }
        });

        socket.on(EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String event = data.getString("event");
                    String message = data.getString("message");
                    
                    Log.e(TAG, "Socket error - Event: " + event + ", Message: " + message);
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing error event", e);
                }
            }
        });
    }

    // Public methods
    public void connect() {
        try {
            if (socket != null && !socket.connected()) {
                Log.d(TAG, "Attempting to connect socket");
                socket.connect();
            } else if (socket == null) {
                Log.e(TAG, "Socket is null, cannot connect. Call init() first.");
                // Try to reinitialize if context is available
                if (context != null) {
                    Log.d(TAG, "Attempting to reinitialize socket");
                    init(context);
                    if (socket != null) {
                        socket.connect();
                    }
                }
            } else {
                Log.d(TAG, "Socket already connected");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error connecting socket", e);
        }
    }
    
    public boolean isInitialized() {
        return socket != null;
    }
    
    public void debugStatus() {
        Log.d(TAG, "=== Socket Debug Status ===");
        Log.d(TAG, "Socket instance: " + (socket != null ? "EXISTS" : "NULL"));
        Log.d(TAG, "Context: " + (context != null ? "EXISTS" : "NULL"));
        Log.d(TAG, "WebSocket URL: " + ApiConfig.WEBSOCKET_URL);
        Log.d(TAG, "Is connected: " + isConnected());
        Log.d(TAG, "Socket connected: " + (socket != null ? socket.connected() : "N/A"));
        Log.d(TAG, "=========================");
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.connected() && isConnected;
    }

    public void joinConversation(String conversationId) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("conversationId", conversationId);
                socket.emit(EVENT_JOIN_CONVERSATION, data);
                Log.d(TAG, "Joining conversation: " + conversationId);
            } catch (JSONException e) {
                Log.e(TAG, "Error joining conversation", e);
            }
        }
    }

    public void leaveConversation(String conversationId) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("conversationId", conversationId);
                socket.emit(EVENT_LEAVE_CONVERSATION, data);
                Log.d(TAG, "Leaving conversation: " + conversationId);
                
                if (conversationId.equals(currentConversationId)) {
                    currentConversationId = null;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error leaving conversation", e);
            }
        }
    }

    public void sendMessage(String conversationId, String content) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("conversationId", conversationId);
                data.put("content", content);
                socket.emit(EVENT_SEND_MESSAGE, data);
                Log.d(TAG, "Sending message to conversation: " + conversationId);
            } catch (JSONException e) {
                Log.e(TAG, "Error sending message", e);
            }
        }
    }

    public void startConversation(String subject, String initialMessage) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("subject", subject);
                data.put("initialMessage", initialMessage);
                socket.emit(EVENT_START_CONVERSATION, data);
                Log.d(TAG, "Starting new conversation");
            } catch (JSONException e) {
                Log.e(TAG, "Error starting conversation", e);
            }
        }
    }

    public void sendTyping(String conversationId, boolean isTyping) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("conversationId", conversationId);
                data.put("isTyping", isTyping);
                socket.emit(EVENT_TYPING, data);
            } catch (JSONException e) {
                Log.e(TAG, "Error sending typing", e);
            }
        }
    }

    // Helper methods
    private ChatMessage parseMessage(JSONObject messageJson) throws JSONException {
        ChatMessage message = new ChatMessage();
        message.setId(messageJson.getString("_id"));
        message.setMessage(messageJson.getString("content"));
        message.setSenderId(messageJson.optString("sender_id", ""));
        message.setSenderName(messageJson.optString("sender_name", ""));
        
        // Parse timestamp
        String timestampStr = messageJson.optString("created_at");
        if (!timestampStr.isEmpty()) {
            // Parse ISO timestamp or use current time
            message.setTimestamp(new Date()); // Simplified for now
        } else {
            message.setTimestamp(new Date());
        }
        
        return message;
    }

    private String getAuthToken() {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getString("auth_token", null);
    }

    private String getOrCreateSessionId() {
        if (context == null) return "sess_" + System.currentTimeMillis() + "_" + Math.random();
        
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String sessionId = prefs.getString("session_id", null);
        
        if (sessionId == null || sessionId.isEmpty()) {
            // Use same format as NetworkClient
            sessionId = "sess_" + System.currentTimeMillis() + "_" + Math.random();
            prefs.edit().putString("session_id", sessionId).apply();
            Log.d(TAG, "Generated new session ID for WebSocket: " + sessionId);
        }
        
        return sessionId;
    }

    // Listener management
    public void addMessageListener(OnMessageListener listener) {
        if (!messageListeners.contains(listener)) {
            messageListeners.add(listener);
        }
    }

    public void removeMessageListener(OnMessageListener listener) {
        messageListeners.remove(listener);
    }

    public void addTypingListener(OnTypingListener listener) {
        if (!typingListeners.contains(listener)) {
            typingListeners.add(listener);
        }
    }

    public void removeTypingListener(OnTypingListener listener) {
        typingListeners.remove(listener);
    }

    public void addConnectionListener(OnConnectionListener listener) {
        if (!connectionListeners.contains(listener)) {
            connectionListeners.add(listener);
        }
    }

    public void removeConnectionListener(OnConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    // Notification methods
    private void notifyNewMessage(ChatMessage message, String conversationId) {
        for (OnMessageListener listener : messageListeners) {
            listener.onNewMessage(message, conversationId);
        }
    }

    private void notifyTypingChanged(String userId, String userName, boolean isTyping, String conversationId) {
        for (OnTypingListener listener : typingListeners) {
            listener.onTypingChanged(userId, userName, isTyping, conversationId);
        }
    }

    private void notifyConnectionChanged(boolean connected) {
        for (OnConnectionListener listener : connectionListeners) {
            listener.onConnectionChanged(connected);
        }
    }

    // Listener interfaces
    public interface OnMessageListener {
        void onNewMessage(ChatMessage message, String conversationId);
    }

    public interface OnTypingListener {
        void onTypingChanged(String userId, String userName, boolean isTyping, String conversationId);
    }

    public interface OnConnectionListener {
        void onConnectionChanged(boolean connected);
    }
} 