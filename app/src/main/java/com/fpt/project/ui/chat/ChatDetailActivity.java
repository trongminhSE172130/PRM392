package com.fpt.project.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.ChatConversation;
import com.fpt.project.data.model.ChatMessage;
import com.fpt.project.data.network.NetworkClient;
import com.fpt.project.data.network.SocketManager;
import com.fpt.project.data.model.request.SendMessageRequest;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.response.MessagesResponse;
import com.fpt.project.data.model.response.SendMessageResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatDetailActivity extends AppCompatActivity implements 
        SocketManager.OnMessageListener, 
        SocketManager.OnTypingListener,
        SocketManager.OnConnectionListener {

    private static final String TAG = "ChatDetailActivity";
    private static final String EXTRA_CONVERSATION = "extra_conversation";
    
    // Views
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private TextView tvOnlineStatus;
    private ImageView ivToolbarAvatar;
    private RecyclerView recyclerViewMessages;
    private View layoutLoadingMessages;
    private View layoutEmptyMessages;
    private EditText etMessage;
    private FloatingActionButton btnSend;
    private ImageView btnAttachment;
    private View layoutTypingIndicator;
    private View layoutFilePreview;
    
    // Data
    private ChatConversation conversation;
    private ChatMessageAdapter messageAdapter;
    private List<ChatMessage> messageList;
    private String currentUserId = "current_user"; // This should come from user session
    
    // WebSocket
    private SocketManager socketManager;
    private boolean isTyping = false;

    public static Intent createIntent(Context context, ChatConversation conversation) {
        Intent intent = new Intent(context, ChatDetailActivity.class);
        intent.putExtra(EXTRA_CONVERSATION, conversation);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Handle edge-to-edge display and system bars
        setupSystemBars();
        
        setContentView(R.layout.activity_chat_detail);

        getDataFromIntent();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupMessageInput();
        setupWebSocket();
        loadMessages();
    }

    private void setupSystemBars() {
        // Ensure content doesn't get hidden behind system bars (notch, status bar, navigation bar)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // For Android 11+ (API 30+) - use WindowInsetsController
            getWindow().setDecorFitsSystemWindows(true);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // For Android 5.0+ (API 21+) - use system UI flags
            getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    @SuppressWarnings("deprecation")
    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_CONVERSATION)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                conversation = getIntent().getSerializableExtra(EXTRA_CONVERSATION, ChatConversation.class);
            } else {
                conversation = (ChatConversation) getIntent().getSerializableExtra(EXTRA_CONVERSATION);
            }
        }
        
        if (conversation == null) {
            Toast.makeText(this, "Conversation not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvOnlineStatus = findViewById(R.id.tvOnlineStatus);
        ivToolbarAvatar = findViewById(R.id.ivToolbarAvatar);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        layoutLoadingMessages = findViewById(R.id.layoutLoadingMessages);
        layoutEmptyMessages = findViewById(R.id.layoutEmptyMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnAttachment = findViewById(R.id.btnAttachment);
        layoutTypingIndicator = findViewById(R.id.layoutTypingIndicator);
        layoutFilePreview = findViewById(R.id.layoutFilePreview);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(""); // We'll use custom title
        }

        // Set conversation info in toolbar
        if (conversation != null) {
            tvToolbarTitle.setText(conversation.getUserName());
            
            if (conversation.isOnline()) {
                tvOnlineStatus.setText("Online");
                tvOnlineStatus.setTextColor(getColor(android.R.color.holo_green_dark));
            } else {
                tvOnlineStatus.setText("Offline");
                tvOnlineStatus.setTextColor(getColor(android.R.color.darker_gray));
            }
        }
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(messageList, currentUserId);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupMessageInput() {
        // Enable/disable send button based on text input and handle typing indicator
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s.toString().trim().length() > 0;
                
                // Only enable if we have text and button isn't disabled due to sending
                if (hasText && (btnSend.getTag() == null || !"sending".equals(btnSend.getTag()))) {
                    btnSend.setEnabled(true);
                } else if (!hasText) {
                    btnSend.setEnabled(false);
                }
                
                // Handle typing indicator
                if (socketManager != null && socketManager.isConnected() && 
                    conversation != null && conversation.getId() != null) {
                    
                    if (hasText && !isTyping) {
                        isTyping = true;
                        socketManager.sendTyping(conversation.getId(), true);
                    } else if (!hasText && isTyping) {
                        isTyping = false;
                        socketManager.sendTyping(conversation.getId(), false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Send button click
        btnSend.setOnClickListener(v -> sendMessage());

        // Attachment button click
        btnAttachment.setOnClickListener(v -> {
            Toast.makeText(this, "Attachment feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupWebSocket() {
        socketManager = SocketManager.getInstance();
        
        // Don't re-initialize if already initialized, just reuse existing connection
        if (!socketManager.isInitialized()) {
            Log.d(TAG, "Initializing SocketManager in ChatDetailActivity");
            socketManager.init(this);
        } else {
            Log.d(TAG, "SocketManager already initialized, reusing connection");
        }
        
        // Always add listeners for this activity
        socketManager.addMessageListener(this);
        socketManager.addTypingListener(this);
        socketManager.addConnectionListener(this);
        
        // Connect if not already connected
        if (!socketManager.isConnected()) {
            Log.d(TAG, "Socket not connected, attempting to connect");
            socketManager.connect();
        } else {
            Log.d(TAG, "Socket already connected");
        }
        
        // Join conversation if available
        if (conversation != null && conversation.getId() != null) {
            Log.d(TAG, "Joining conversation: " + conversation.getId());
            socketManager.joinConversation(conversation.getId());
        }
    }

    private void loadMessages() {
        showLoading(true);
        
        if (conversation == null || conversation.getId() == null) {
            Log.w(TAG, "No conversation available");
            showEmpty(true);
            return;
        }
        
        Log.d(TAG, "Loading messages for conversation: " + conversation.getId());
        // Call API to get messages
        NetworkClient.getInstance(this).getApiService()
            .getChatMessages(conversation.getId(), 1, 50)
            .enqueue(new Callback<ApiResponse<MessagesResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<MessagesResponse>> call, Response<ApiResponse<MessagesResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<MessagesResponse> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            MessagesResponse messagesResponse = apiResponse.getData();
                            if (messagesResponse != null && messagesResponse.getMessages() != null) {
                                List<ChatMessage> apiMessages = messagesResponse.getMessages();
                                if (!apiMessages.isEmpty()) {
                                    Log.d(TAG, "Loaded " + apiMessages.size() + " messages from API");
                                    messageList.clear();
                                    
                                    // Show all messages: customer, store, and system
                                    for (ChatMessage message : apiMessages) {
                                        messageList.add(message);
                                        Log.d(TAG, "Added message from " + message.getSenderType() + ": " + message.getMessage());
                                    }
                                    
                                    Log.d(TAG, "Showing " + messageList.size() + " messages (all types)");
                                    messageAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d(TAG, "No messages found");
                                    messageList.clear();
                                }
                            } else {
                                Log.d(TAG, "MessagesResponse or messages is null");
                                messageList.clear();
                            }
                        } else {
                            Log.e(TAG, "API error: " + apiResponse.getMessage());
                            messageList.clear();
                            Toast.makeText(ChatDetailActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "HTTP error: " + response.code());
                        messageList.clear();
                        Toast.makeText(ChatDetailActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                    }
                    showMessagesContent();
                }

                @Override
                public void onFailure(Call<ApiResponse<MessagesResponse>> call, Throwable t) {
                    Log.e(TAG, "Network error loading messages: " + t.getMessage());
                    messageList.clear();
                    showMessagesContent();
                    Toast.makeText(ChatDetailActivity.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showMessagesContent() {
        showLoading(false);
        if (messageList.isEmpty()) {
            showEmpty(true);
        } else {
            showMessages(true);
            scrollToBottom();
        }
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) return;

        // Validate conversation exists
        if (conversation == null || conversation.getId() == null) {
            Toast.makeText(this, "No conversation available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear message input immediately
        etMessage.setText("");
        
        // Disable send button temporarily and mark as sending
        btnSend.setEnabled(false);
        btnSend.setTag("sending");

        // Stop typing indicator if active
        if (isTyping) {
            isTyping = false;
            if (socketManager != null && socketManager.isConnected()) {
                socketManager.sendTyping(conversation.getId(), false);
            }
        }

        // Create optimistic local message (show immediately)
        ChatMessage optimisticMessage = new ChatMessage();
        optimisticMessage.setId("temp_" + System.currentTimeMillis());
        optimisticMessage.setSenderId(currentUserId); // Use currentUserId for consistency
        optimisticMessage.setSenderType("customer");
        optimisticMessage.setMessage(messageText);
        optimisticMessage.setTimestamp(new Date());
        optimisticMessage.setRead(false); // Not read yet

        // Add to UI immediately for better UX
        messageAdapter.addMessage(optimisticMessage);
        scrollToBottom();

        // Send via API
        sendMessageViaApi(messageText, optimisticMessage);
    }

    private void sendMessageViaApi(String messageText, ChatMessage optimisticMessage) {
        Log.d(TAG, "Sending message via API: " + messageText);
        Log.d(TAG, "Conversation ID: " + conversation.getId());
        
        // Create request object
        SendMessageRequest request = new SendMessageRequest(messageText);
        
        NetworkClient.getInstance(this).getApiService()
            .sendMessage(conversation.getId(), request)
            .enqueue(new Callback<SendMessageResponse>() {
                @Override
                public void onResponse(Call<SendMessageResponse> call, Response<SendMessageResponse> response) {
                    runOnUiThread(() -> {
                        // Re-enable send button and clear sending state
                        btnSend.setTag(null);
                        btnSend.setEnabled(!etMessage.getText().toString().trim().isEmpty());
                        
                        if (response.isSuccessful() && response.body() != null) {
                            SendMessageResponse apiResponse = response.body();
                            Log.d(TAG, "Message API response: " + apiResponse.toString());
                            
                            if (apiResponse.isSuccess()) {
                                ChatMessage serverMessage = apiResponse.getData();
                                if (serverMessage != null) {
                                    // Log detailed message info for debugging
                                    Log.d(TAG, "Server message ID: " + serverMessage.getId());
                                    Log.d(TAG, "Conversation ID String: " + serverMessage.getConversationIdString());
                                    Log.d(TAG, "Conversation Info: " + serverMessage.getConversationId());
                                    Log.d(TAG, "Sender ID: " + serverMessage.getSender());
                                    Log.d(TAG, "Sender Type: " + serverMessage.getSenderType());
                                    Log.d(TAG, "Message Content: " + serverMessage.getMessage());
                                    
                                    // Replace optimistic message with server message
                                    replaceOptimisticMessage(optimisticMessage, serverMessage);
                                    Log.d(TAG, "Message sent successfully: " + serverMessage.getId());
                                } else {
                                    Log.w(TAG, "Server message is null");
                                    markOptimisticMessageAsDelivered(optimisticMessage);
                                }
                            } else {
                                Log.e(TAG, "API error: " + apiResponse.getMessage());
                                handleMessageSendError(optimisticMessage, "Server error: " + apiResponse.getMessage());
                            }
                        } else {
                            Log.e(TAG, "HTTP error: " + response.code());
                            String errorMsg = "HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg += ": " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            handleMessageSendError(optimisticMessage, errorMsg);
                        }
                    });
                }

                @Override
                public void onFailure(Call<SendMessageResponse> call, Throwable t) {
                    Log.e(TAG, "Network error sending message: " + t.getMessage(), t);
                    runOnUiThread(() -> {
                        // Re-enable send button and clear sending state
                        btnSend.setTag(null);
                        btnSend.setEnabled(!etMessage.getText().toString().trim().isEmpty());
                        handleMessageSendError(optimisticMessage, "Network error: " + t.getMessage());
                    });
                }
            });
    }
    
    private void replaceOptimisticMessage(ChatMessage optimisticMessage, ChatMessage serverMessage) {
        // Find and replace the optimistic message with server message
        boolean found = false;
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getId().equals(optimisticMessage.getId())) {
                Log.d(TAG, "Replacing optimistic message at position " + i + " with server message");
                messageList.set(i, serverMessage);
                messageAdapter.notifyItemChanged(i);
                found = true;
                break;
            }
        }
        
        if (!found) {
            Log.w(TAG, "Could not find optimistic message to replace, ID: " + optimisticMessage.getId());
            // Don't add server message if we can't find optimistic one to replace
            // This prevents duplicate messages
        }
    }
    
    private void markOptimisticMessageAsDelivered(ChatMessage optimisticMessage) {
        // Update optimistic message status
        optimisticMessage.setId("sent_" + System.currentTimeMillis());
        // Could add delivered status here
        messageAdapter.notifyDataSetChanged();
    }
    
    private void handleMessageSendError(ChatMessage optimisticMessage, String error) {
        Log.e(TAG, "Message send failed: " + error);
        // Could mark message as failed, add retry button, etc.
        // For now, just show error but keep message visible
        Toast.makeText(this, "Message delivery failed", Toast.LENGTH_SHORT).show();
        
        // Update message to show error state
        optimisticMessage.setId("failed_" + optimisticMessage.getId());
        messageAdapter.notifyDataSetChanged();
    }

    private void showLoading(boolean show) {
        layoutLoadingMessages.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewMessages.setVisibility(View.GONE);
        layoutEmptyMessages.setVisibility(View.GONE);
    }

    private void showMessages(boolean show) {
        layoutLoadingMessages.setVisibility(View.GONE);
        recyclerViewMessages.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutEmptyMessages.setVisibility(View.GONE);
    }

    private void showEmpty(boolean show) {
        layoutLoadingMessages.setVisibility(View.GONE);
        recyclerViewMessages.setVisibility(View.GONE);
        layoutEmptyMessages.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void scrollToBottom() {
        if (messageAdapter.getItemCount() > 0) {
            recyclerViewMessages.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ChatDetailActivity onResume");
        
        // Ensure socket connection is maintained when coming back to this activity
        if (socketManager != null) {
            if (!socketManager.isConnected()) {
                Log.d(TAG, "Socket disconnected, attempting to reconnect");
                socketManager.connect();
            }
            
            // Re-join conversation if needed
            if (conversation != null && conversation.getId() != null) {
                Log.d(TAG, "Re-joining conversation on resume: " + conversation.getId());
                socketManager.joinConversation(conversation.getId());
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ChatDetailActivity onPause");
        // Don't leave conversation on pause, only on back/destroy
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        Log.d(TAG, "ChatDetailActivity onBackPressed");
        // Leave conversation room when going back
        if (socketManager != null && conversation != null && conversation.getId() != null) {
            Log.d(TAG, "Leaving conversation on back pressed: " + conversation.getId());
            socketManager.leaveConversation(conversation.getId());
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socketManager != null) {
            // Remove listeners for this activity
            socketManager.removeMessageListener(this);
            socketManager.removeTypingListener(this);
            socketManager.removeConnectionListener(this);
            
            // Leave conversation room but keep socket connection for other activities
            if (conversation != null && conversation.getId() != null) {
                Log.d(TAG, "Leaving conversation on destroy: " + conversation.getId());
                socketManager.leaveConversation(conversation.getId());
            }
            
            // DON'T disconnect socket here - let ChatFragment manage the main connection
            Log.d(TAG, "ChatDetailActivity destroyed, keeping socket connection alive");
        }
    }

    // SocketManager.OnMessageListener implementation
    @Override
    public void onNewMessage(ChatMessage message, String conversationId) {
        runOnUiThread(() -> {
            if (conversation != null && conversation.getId().equals(conversationId)) {
                // Skip adding message if it's from current user (already handled via optimistic UI)
                if (message.getSenderId() != null && 
                    (message.getSenderId().equals(currentUserId) || 
                     message.getSenderId().equals("current_user") ||
                     "customer".equals(message.getSenderType()))) {
                    Log.d(TAG, "Skipping duplicate message from current user: " + message.getId());
                    return;
                }
                
                // Add messages from store/system (support agents, etc.)
                Log.d(TAG, "Adding new message from: " + message.getSenderId() + " (" + message.getSenderType() + ")");
                messageAdapter.addMessage(message);
                scrollToBottom();
                
                // Hide typing indicator if it was shown
                layoutTypingIndicator.setVisibility(View.GONE);
            }
        });
    }

    // SocketManager.OnTypingListener implementation
    @Override
    public void onTypingChanged(String userId, String userName, boolean isTyping, String conversationId) {
        runOnUiThread(() -> {
            if (conversation != null && conversation.getId().equals(conversationId) && 
                !userId.equals(currentUserId)) {
                
                // Show/hide typing indicator for other users
                layoutTypingIndicator.setVisibility(isTyping ? View.VISIBLE : View.GONE);
                
                if (isTyping) {
                    // Scroll to bottom to show typing indicator
                    scrollToBottom();
                }
            }
        });
    }

    // SocketManager.OnConnectionListener implementation
    @Override
    public void onConnectionChanged(boolean connected) {
        Log.d(TAG, "Connection changed in ChatDetailActivity: " + connected);
        
        // Check if activity is still valid before updating UI
        if (isFinishing() || isDestroyed()) {
            Log.d(TAG, "Activity is finishing/destroyed, ignoring connection change");
            return;
        }
        
        runOnUiThread(() -> {
            try {
                if (connected) {
                    Log.d(TAG, "Socket connected, rejoining conversation");
                    // Rejoin conversation when reconnected
                    if (conversation != null && conversation.getId() != null) {
                        Log.d(TAG, "Rejoining conversation: " + conversation.getId());
                        socketManager.joinConversation(conversation.getId());
                    }
                    // Don't show toast as it might interfere with user experience
                    Log.d(TAG, "Connected to chat server");
                } else {
                    Log.w(TAG, "Socket disconnected in ChatDetailActivity");
                    // Don't show toast for disconnect either to avoid interruptions
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling connection change", e);
            }
        });
    }
} 