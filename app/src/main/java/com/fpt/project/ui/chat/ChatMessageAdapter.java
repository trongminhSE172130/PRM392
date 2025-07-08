package com.fpt.project.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.ChatMessage;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private String currentUserId;
    
    public ChatMessageAdapter(List<ChatMessage> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message, position);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        // Received message views
        private View layoutReceivedMessage;
        private TextView tvReceivedMessage;
        private TextView tvReceivedTimestamp;
        private TextView tvSenderName;
        private ImageView ivSenderAvatar;
        private ImageView ivReceivedImage;

        // Sent message views
        private View layoutSentMessage;
        private TextView tvSentMessage;
        private TextView tvSentTimestamp;
        private ImageView ivUserAvatar;
        private ImageView ivMessageStatus;
        private ImageView ivSentImage;

        // System message views
        private View layoutSystemMessage;
        private TextView tvSystemMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Initialize received message views
            layoutReceivedMessage = itemView.findViewById(R.id.layoutReceivedMessage);
            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedTimestamp = itemView.findViewById(R.id.tvReceivedTimestamp);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            ivSenderAvatar = itemView.findViewById(R.id.ivSenderAvatar);
            ivReceivedImage = itemView.findViewById(R.id.ivReceivedImage);

            // Initialize sent message views
            layoutSentMessage = itemView.findViewById(R.id.layoutSentMessage);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentTimestamp = itemView.findViewById(R.id.tvSentTimestamp);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            ivMessageStatus = itemView.findViewById(R.id.ivMessageStatus);
            ivSentImage = itemView.findViewById(R.id.ivSentImage);

            // Initialize system message views
            layoutSystemMessage = itemView.findViewById(R.id.layoutSystemMessage);
            tvSystemMessage = itemView.findViewById(R.id.tvSystemMessage);
        }

        public void bind(ChatMessage message, int position) {
            // Hide all layouts first
            layoutReceivedMessage.setVisibility(View.GONE);
            layoutSentMessage.setVisibility(View.GONE);
            layoutSystemMessage.setVisibility(View.GONE);

            // Check if this is a system message
            if (isSystemMessage(message)) {
                bindSystemMessage(message);
                return;
            }

            // Show all messages - customer, store, and system
            boolean isSentByCurrentUser = isMessageFromCurrentUser(message);

            if (isSentByCurrentUser) {
                bindSentMessage(message, position);
            } else {
                bindReceivedMessage(message, position);
            }
            
            // Ensure the item is visible
            itemView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (params != null) {
                params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                itemView.setLayoutParams(params);
            }
        }

        private void bindReceivedMessage(ChatMessage message, int position) {
            layoutReceivedMessage.setVisibility(View.VISIBLE);

            // Set message text
            tvReceivedMessage.setText(message.getMessage());

            // Set timestamp
            tvReceivedTimestamp.setText(formatTimeFromMessage(message));

            // Set sender name based on sender type
            if ("store".equals(message.getSenderType())) {
                tvSenderName.setText("Store Support");
                tvSenderName.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                tvSenderName.setVisibility(View.VISIBLE);
            } else {
                // Hide sender name completely for all other cases including Anonymous Customer
                tvSenderName.setVisibility(View.GONE);
            }

            // Load sender avatar based on type
            if (ivSenderAvatar != null) {
                if (message.isFromStore()) {
                    ivSenderAvatar.setImageResource(R.drawable.ic_settings); // Store icon
                } else {
                    ivSenderAvatar.setImageResource(R.drawable.ic_person); // Customer icon
                }
            }

            // Handle image attachment
            View cvReceivedImage = itemView.findViewById(R.id.cvReceivedImage);
            if (message.hasAttachments() && message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                cvReceivedImage.setVisibility(View.VISIBLE);
                ivReceivedImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(ivReceivedImage);
            } else {
                cvReceivedImage.setVisibility(View.GONE);
                ivReceivedImage.setVisibility(View.GONE);
            }

            // Show read status if applicable
            TextView tvReceivedStatus = itemView.findViewById(R.id.tvReceivedStatus);
            if (tvReceivedStatus != null) {
                if (message.isRead()) {
                    tvReceivedStatus.setText(" • Read");
                    tvReceivedStatus.setVisibility(View.VISIBLE);
                } else {
                    tvReceivedStatus.setVisibility(View.GONE);
                }
            }
        }

        private void bindSentMessage(ChatMessage message, int position) {
            layoutSentMessage.setVisibility(View.VISIBLE);

            // Set message text
            tvSentMessage.setText(message.getMessage());

            // Set timestamp
            tvSentTimestamp.setText(formatTimeFromMessage(message));

            // Set message status icon based on read status
            if (ivMessageStatus != null) {
                if (message.isRead()) {
                    // Double checkmark for read
                    ivMessageStatus.setImageResource(R.drawable.ic_chat);
                    ivMessageStatus.setColorFilter(itemView.getContext().getColor(android.R.color.holo_blue_bright));
                } else {
                    // Single checkmark for delivered
                    ivMessageStatus.setImageResource(R.drawable.ic_chat);
                    ivMessageStatus.setColorFilter(itemView.getContext().getColor(android.R.color.darker_gray));
                }
                ivMessageStatus.setVisibility(View.VISIBLE);
            }

            // Load user avatar
            if (ivUserAvatar != null) {
                ivUserAvatar.setImageResource(R.drawable.ic_person);
            }

            // Handle image attachment
            View cvSentImage = itemView.findViewById(R.id.cvSentImage);
            if (message.hasAttachments() && message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                cvSentImage.setVisibility(View.VISIBLE);
                ivSentImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(ivSentImage);
            } else {
                cvSentImage.setVisibility(View.GONE);
                ivSentImage.setVisibility(View.GONE);
            }
        }

        private void bindSystemMessage(ChatMessage message) {
            layoutSystemMessage.setVisibility(View.VISIBLE);
            tvSystemMessage.setText(message.getMessage());
        }

        private boolean isSystemMessage(ChatMessage message) {
            // Check if message type is system or sender type is system
            return "system".equals(message.getMessageType()) || 
                   "system".equals(message.getSenderType()) ||
                   message.isSystemMessage();
        }

        private String formatTime(Date timestamp) {
            if (timestamp == null) return "";

            Date now = new Date();
            long diff = now.getTime() - timestamp.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays == 0) {
                // Today - show time only
                return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp);
            } else if (diffDays == 1) {
                // Yesterday
                return "Yesterday " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp);
            } else if (diffDays < 7) {
                // This week - show day and time
                return new SimpleDateFormat("EEE HH:mm", Locale.getDefault()).format(timestamp);
            } else {
                // Older - show date and time
                return new SimpleDateFormat("MMM dd HH:mm", Locale.getDefault()).format(timestamp);
            }
        }
        
        // Alternative method using ChatMessage's utility methods
        private String formatTimeFromMessage(ChatMessage message) {
            if (message == null) return "";
            
            String time = message.getFormattedTime();
            String date = message.getFormattedDate();
            
            if (time.isEmpty()) return "";
            
            // Check if today
            String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            if (date.equals(todayDate)) {
                return time;
            } else {
                return date + " " + time;
            }
        }
    }

    // Check if message is from current user
    private boolean isMessageFromCurrentUser(ChatMessage message) {
        if (message == null) return false;
        
        // Check by sender ID first (most reliable)
        if (message.getSenderId() != null && currentUserId != null) {
            return message.getSenderId().equals(currentUserId) || 
                   message.getSenderId().equals("current_user");
        }
        
        // Fallback to sender type for optimistic messages
        if (message.isFromCustomer()) {
            return true;
        }
        
        // Additional check for temporary/optimistic messages
        if (message.getId() != null && 
            (message.getId().startsWith("temp_") || 
             message.getId().startsWith("sent_") ||
             message.getId().startsWith("failed_"))) {
            return true;
        }
        
        return false;
    }

    // Helper methods
    public void addMessage(ChatMessage message) {
        if (messages != null) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }
    }

    public void addMessages(List<ChatMessage> newMessages) {
        if (messages != null && newMessages != null) {
            int startPosition = messages.size();
            messages.addAll(newMessages);
            notifyItemRangeInserted(startPosition, newMessages.size());
        }
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void clearMessages() {
        if (messages != null) {
            messages.clear();
            notifyDataSetChanged();
        }
    }
} 