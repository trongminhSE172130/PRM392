package com.fpt.project.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.ChatConversation;
import com.fpt.project.data.network.ApiConfig;
import com.fpt.project.data.network.NetworkClient;
import com.fpt.project.data.network.SocketManager;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.ChatMessage;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment implements 
        ChatConversationAdapter.OnConversationClickListener,
        SocketManager.OnMessageListener,
        SocketManager.OnConnectionListener {

    private static final String TAG = "ChatFragment";

    // Views
    private RecyclerView recyclerViewChats;
    private View layoutLoading;
    private View layoutEmpty;
    private View layoutError;
    private EditText etSearchChat;
    private MaterialButton btnNewChat;
    private MaterialButton btnStartChat;
    private MaterialButton btnRetry;

    // Data
    private ChatConversationAdapter conversationAdapter;
    private List<ChatConversation> conversationList;
    private List<ChatConversation> filteredConversationList;
    
    // WebSocket
    private SocketManager socketManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initViews(view);
        setupRecyclerView();
        setupSearchFunctionality();
        setupClickListeners();
        setupWebSocket();
        loadConversations();
        return view;
    }

    private void initViews(View view) {
        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        etSearchChat = view.findViewById(R.id.etSearchChat);
        btnNewChat = view.findViewById(R.id.btnNewChat);
        btnStartChat = view.findViewById(R.id.btnStartChat);
        btnRetry = view.findViewById(R.id.btnRetry);
    }

    private void setupRecyclerView() {
        conversationList = new ArrayList<>();
        filteredConversationList = new ArrayList<>();
        conversationAdapter = new ChatConversationAdapter(filteredConversationList, this);
        
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChats.setAdapter(conversationAdapter);
    }

    private void setupSearchFunctionality() {
        etSearchChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterConversations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        btnNewChat.setOnClickListener(v -> startNewChat());
        btnStartChat.setOnClickListener(v -> startNewChat());
        btnRetry.setOnClickListener(v -> loadConversations());
    }

    private void setupWebSocket() {
        try {
            socketManager = SocketManager.getInstance();
            
            // Safely check context
            if (getContext() == null) {
                Log.e(TAG, "Context is null, cannot setup WebSocket");
                return;
            }
            
            socketManager.init(getContext());
            
            // Check if initialization was successful
            if (!socketManager.isInitialized()) {
                Log.e(TAG, "Socket initialization failed");
                socketManager.debugStatus(); // Debug info
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Chat service unavailable", Toast.LENGTH_SHORT).show();
                }
                return;
            } else {
                socketManager.debugStatus(); // Debug info
            }
            
            socketManager.addMessageListener(this);
            socketManager.addConnectionListener(this);
            
            // Connect with delay to avoid race conditions
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                try {
                    if (socketManager != null && socketManager.isInitialized() && getContext() != null) {
                        socketManager.connect();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error connecting socket with delay", e);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Chat connection failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up WebSocket", e);
            // Show error without crashing
            if (getContext() != null) {
                Toast.makeText(getContext(), "Chat initialization failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadConversations() {
        showLoading(true);
        loadConversationsFromApi();
    }

    private void loadConversationsFromApi() {
        Log.d(TAG, "Loading user conversations from API: " + ApiConfig.CONVERSATION_BY_USER);
        NetworkClient.getInstance(getContext()).getApiService()
            .getMyConversations()
            .enqueue(new Callback<ApiResponse<List<Object>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Object>>> call, Response<ApiResponse<List<Object>>> response) {
                    if (getContext() == null) return;
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<List<Object>> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            List<Object> apiConversations = apiResponse.getData();
                            if (apiConversations != null && !apiConversations.isEmpty()) {
                                Log.d(TAG, "Found " + apiConversations.size() + " conversations");
                                parseApiConversations(apiConversations);
                                showContent(true);
                            } else {
                                Log.d(TAG, "No conversations found");
                                showEmpty(true);
                            }
                        } else {
                            Log.e(TAG, "API error: " + apiResponse.getMessage());
                            showEmpty(true);
                            Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "HTTP error: " + response.code());
                        showEmpty(true);
                        Toast.makeText(getContext(), "Unable to load conversations", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Object>>> call, Throwable t) {
                    if (getContext() == null) return;
                    
                    Log.e(TAG, "Network error: " + t.getMessage());
                    showEmpty(true);
                    Toast.makeText(getContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void parseApiConversations(List<Object> apiConversations) {
        conversationList.clear();
        
        try {
            // Parse all conversations for the user
            for (Object conversationObj : apiConversations) {
                if (conversationObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> conversationData = (Map<String, Object>) conversationObj;
                    ChatConversation conversation = parseConversationFromApi(conversationData);
                    if (conversation != null) {
                        conversationList.add(conversation);
                        Log.d(TAG, "Parsed conversation: " + conversation.getId() + " - " + conversation.getUserName());
                    }
                }
            }
            
            Log.d(TAG, "Successfully parsed " + conversationList.size() + " conversations");
        } catch (Exception e) {
            Log.e(TAG, "Error parsing conversations", e);
            conversationList.clear();
        }
        
        // Update filtered list
        filteredConversationList.clear();
        filteredConversationList.addAll(conversationList);
        conversationAdapter.notifyDataSetChanged();
    }



    private void filterConversations(String query) {
        filteredConversationList.clear();
        
        if (query.isEmpty()) {
            filteredConversationList.addAll(conversationList);
        } else {
            String lowercaseQuery = query.toLowerCase();
            for (ChatConversation conversation : conversationList) {
                if (conversation.getUserName().toLowerCase().contains(lowercaseQuery) ||
                    (conversation.getLastMessage() != null && 
                     conversation.getLastMessage().toLowerCase().contains(lowercaseQuery))) {
                    filteredConversationList.add(conversation);
                }
            }
        }
        
        conversationAdapter.notifyDataSetChanged();
        
        // Show empty state if no results
        if (filteredConversationList.isEmpty() && !query.isEmpty()) {
            showEmpty(true);
        } else if (!filteredConversationList.isEmpty()) {
            showContent(true);
        }
    }

    private void startNewChat() {
        // Show loading
        showLoading(true);
        
        // Create new conversation via API
        createNewConversation();
    }
    
    private void createNewConversation() {
        // Prepare conversation request
        Map<String, Object> conversationRequest = new HashMap<>();
        conversationRequest.put("subject", "Product inquiry");
        conversationRequest.put("initialMessage", "Hi, I have a question...");
        
        NetworkClient.getInstance(getContext()).getApiService()
            .createConversation(conversationRequest)
            .enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (getContext() == null) return;
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Object> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "Conversation created successfully");
                            Toast.makeText(getContext(), "New conversation started", Toast.LENGTH_SHORT).show();
                            // Reload conversations to show the new one
                            loadConversations();
                        } else {
                            Log.e(TAG, "Failed to create conversation: " + apiResponse.getMessage());
                            showEmpty(true);
                            Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "HTTP error creating conversation: " + response.code());
                        showEmpty(true);
                        Toast.makeText(getContext(), "Failed to start conversation", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    if (getContext() == null) return;
                    
                    Log.e(TAG, "Network error creating conversation: " + t.getMessage());
                    showEmpty(true);
                    Toast.makeText(getContext(), "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewChats.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }
    
    private void showEmpty(boolean show) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewChats.setVisibility(View.GONE);
        layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutError.setVisibility(View.GONE);
        
        // Clear data when showing empty
        if (show) {
            conversationList.clear();
            filteredConversationList.clear();
            conversationAdapter.notifyDataSetChanged();
            // Always show new chat button when in empty state
            btnNewChat.setVisibility(View.VISIBLE);
        }
    }

    private void showContent(boolean show) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewChats.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);

        // Show/hide new chat button based on conversation existence
        if (show && !filteredConversationList.isEmpty()) {
            // User has conversation, hide the new chat button
            btnNewChat.setVisibility(View.GONE);
        } else {
            // No conversation, show the new chat button
            btnNewChat.setVisibility(View.VISIBLE);
        }

        // Show empty state if no conversations
        if (show && filteredConversationList.isEmpty()) {
            showEmpty(true);
        }
    }



    private void showError(boolean show, String errorMessage) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewChats.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(show ? View.VISIBLE : View.GONE);
        
        // Set error message if needed
        // You can add error message TextView and set it here
    }

    @Override
    public void onConversationClick(ChatConversation conversation) {
        openChatDetail(conversation);
    }

    private void openChatDetail(ChatConversation conversation) {
        if (getContext() != null) {
            Intent intent = ChatDetailActivity.createIntent(getContext(), conversation);
            startActivity(intent);
        }
    }

    // Optional: Method to refresh conversations when returning from chat detail
    @Override
    public void onResume() {
        super.onResume();
        try {
            if (getContext() != null && socketManager != null) {
                // Ensure SocketManager is properly initialized before connecting
                if (!socketManager.isInitialized()) {
                    Log.d(TAG, "Reinitializing SocketManager in onResume");
                    socketManager.init(getContext());
                }
                
                // Only connect if initialization was successful
                if (socketManager.isInitialized()) {
                    socketManager.connect();
                } else {
                    Log.w(TAG, "SocketManager initialization failed in onResume");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error connecting socket in onResume", e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Keep connection but can implement background handling here
        try {
            if (socketManager != null) {
                // Don't disconnect, just pause
                Log.d(TAG, "Fragment paused, keeping socket connection");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onPause", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (socketManager != null) {
                socketManager.removeMessageListener(this);
                socketManager.removeConnectionListener(this);
                Log.d(TAG, "Socket listeners removed in onDestroy");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up socket in onDestroy", e);
        }
    }

    // SocketManager.OnMessageListener implementation
    @Override
    public void onNewMessage(ChatMessage message, String conversationId) {
        try {
            if (getActivity() != null && getContext() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    try {
                        // Double check context is still valid
                        if (getContext() != null && isAdded()) {
                            // Update conversation list with new message
                            updateConversationWithNewMessage(conversationId, message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling new message on UI thread", e);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onNewMessage", e);
        }
    }

    // SocketManager.OnConnectionListener implementation
    @Override
    public void onConnectionChanged(boolean connected) {
        try {
            if (getActivity() != null && getContext() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    try {
                        // Double check context is still valid
                        if (getContext() != null && isAdded()) {
                            if (connected) {
                                Toast.makeText(getContext(), "Connected to chat server", Toast.LENGTH_SHORT).show();
                                // Load conversations when connected
                                loadConversations();
                            } else {
                                Toast.makeText(getContext(), "Disconnected from chat server", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling connection change on UI thread", e);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onConnectionChanged", e);
        }
    }

    // Parse API conversation response to ChatConversation object
    private ChatConversation parseConversationFromApi(Map<String, Object> conversationData) {
        try {
            String id = (String) conversationData.get("_id");
            if (id == null || id.trim().isEmpty()) {
                Log.w(TAG, "Conversation ID is null or empty, skipping");
                return null;
            }
            
            String customerName = (String) conversationData.get("customer_name");
            String subject = (String) conversationData.get("subject");
            
            ChatConversation conversation = new ChatConversation(id, "support_agent", customerName != null ? customerName : "Customer");
            
            // Set subject or use customer name
            if (subject != null && !subject.isEmpty()) {
                conversation.setUserName(subject);
            }
            
            // Parse last message
            Object lastMessageObj = conversationData.get("last_message");
            if (lastMessageObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> lastMessage = (Map<String, Object>) lastMessageObj;
                String content = (String) lastMessage.get("content");
                String timestampStr = (String) lastMessage.get("timestamp");
                
                conversation.setLastMessage(content != null ? content : "");
                
                // Parse timestamp
                if (timestampStr != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        Date timestamp = sdf.parse(timestampStr);
                        conversation.setLastMessageTime(timestamp);
                    } catch (Exception e) {
                        conversation.setLastMessageTime(new Date());
                    }
                } else {
                    conversation.setLastMessageTime(new Date());
                }
            } else {
                conversation.setLastMessage("");
                conversation.setLastMessageTime(new Date());
            }
            
            // Parse unread count
            Object unreadCountObj = conversationData.get("unread_count");
            if (unreadCountObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> unreadCount = (Map<String, Object>) unreadCountObj;
                Object customerUnreadObj = unreadCount.get("customer");
                if (customerUnreadObj instanceof Number) {
                    conversation.setUnreadCount(((Number) customerUnreadObj).intValue());
                } else {
                    conversation.setUnreadCount(0);
                }
            } else {
                conversation.setUnreadCount(0);
            }
            
            // Set other properties
            String status = (String) conversationData.get("status");
            conversation.setOnline("active".equals(status));
            conversation.setChatType("support");
            
            String priority = (String) conversationData.get("priority");
            conversation.setPriority("high".equals(priority));
            
            return conversation;
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing conversation from API", e);
            return null;
        }
    }

    // Update conversation with new message
    private void updateConversationWithNewMessage(String conversationId, ChatMessage message) {
        for (ChatConversation conversation : conversationList) {
            if (conversation.getId().equals(conversationId)) {
                conversation.setLastMessage(message.getMessage());
                conversation.setLastMessageTime(message.getTimestamp());
                conversation.setUnreadCount(conversation.getUnreadCount() + 1);
                
                // Move to top of list
                conversationList.remove(conversation);
                conversationList.add(0, conversation);
                
                // Update filtered list and notify adapter
                filterConversations(etSearchChat.getText().toString());
                break;
            }
        }
    }

    // Optional: Method to handle new messages (legacy)
    public void onNewMessageReceived(String conversationId, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(new Date());
        updateConversationWithNewMessage(conversationId, chatMessage);
    }
}
