package com.fpt.project.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.ChatConversation;
import com.bumptech.glide.Glide;

import java.util.List;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ConversationViewHolder> {

    private List<ChatConversation> conversations;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(ChatConversation conversation);
    }

    public ChatConversationAdapter(List<ChatConversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        ChatConversation conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations != null ? conversations.size() : 0;
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserAvatar;
        private TextView tvUserName;
        private TextView tvTimestamp;
        private TextView tvLastMessage;
        private TextView tvUnreadBadge;
        private View statusIndicator;
        private TextView tvChatType;
        private ImageView ivChatTypeIcon;
        private View layoutChatType;
        private ImageView ivMessageStatus;
        private ImageView ivPriorityFlag;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvUnreadBadge = itemView.findViewById(R.id.tvUnreadBadge);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            tvChatType = itemView.findViewById(R.id.tvChatType);
            ivChatTypeIcon = itemView.findViewById(R.id.ivChatTypeIcon);
            layoutChatType = itemView.findViewById(R.id.layoutChatType);
            ivMessageStatus = itemView.findViewById(R.id.ivMessageStatus);
            ivPriorityFlag = itemView.findViewById(R.id.ivPriorityFlag);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onConversationClick(conversations.get(getAdapterPosition()));
                }
            });
        }

        public void bind(ChatConversation conversation) {
            // Set user name
            tvUserName.setText(conversation.getUserName());
            
            // Set timestamp
            tvTimestamp.setText(conversation.getFormattedTime());
            
            // Set last message
            if (conversation.getLastMessage() != null && !conversation.getLastMessage().isEmpty()) {
                tvLastMessage.setText(conversation.getLastMessage());
            } else {
                tvLastMessage.setText("No messages yet");
            }

            // Load user avatar
            if (conversation.getUserAvatarUrl() != null && !conversation.getUserAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(conversation.getUserAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivUserAvatar);
            } else {
                ivUserAvatar.setImageResource(R.drawable.ic_person);
            }

            // Show/hide online status
            if (conversation.isOnline()) {
                statusIndicator.setVisibility(View.VISIBLE);
            } else {
                statusIndicator.setVisibility(View.GONE);
            }

            // Show/hide unread badge
            if (conversation.hasUnreadMessages()) {
                tvUnreadBadge.setVisibility(View.VISIBLE);
                tvUnreadBadge.setText(String.valueOf(conversation.getUnreadCount()));
                
                // Bold text for unread conversations
                tvUserName.setTypeface(tvUserName.getTypeface(), android.graphics.Typeface.BOLD);
                tvLastMessage.setTypeface(tvLastMessage.getTypeface(), android.graphics.Typeface.BOLD);
                tvLastMessage.setTextColor(itemView.getContext().getColor(android.R.color.black));
            } else {
                tvUnreadBadge.setVisibility(View.GONE);
                
                // Normal text for read conversations
                tvUserName.setTypeface(tvUserName.getTypeface(), android.graphics.Typeface.NORMAL);
                tvLastMessage.setTypeface(tvLastMessage.getTypeface(), android.graphics.Typeface.NORMAL);
                tvLastMessage.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
            }

            // Show chat type if not general
            if (!"general".equals(conversation.getChatType())) {
                layoutChatType.setVisibility(View.VISIBLE);
                tvChatType.setText(conversation.getChatType().toUpperCase());
                
                // Set appropriate icon based on chat type
                switch (conversation.getChatType().toLowerCase()) {
                    case "support":
                        ivChatTypeIcon.setImageResource(R.drawable.ic_help);
                        break;
                    case "order":
                        ivChatTypeIcon.setImageResource(R.drawable.ic_shopping_cart);
                        break;
                    default:
                        ivChatTypeIcon.setImageResource(R.drawable.ic_chat);
                        break;
                }
            } else {
                layoutChatType.setVisibility(View.GONE);
            }

            // Show priority flag
            if (conversation.isPriority()) {
                ivPriorityFlag.setVisibility(View.VISIBLE);
            } else {
                ivPriorityFlag.setVisibility(View.GONE);
            }

            // Message status (for debugging - normally hidden in conversation list)
            ivMessageStatus.setVisibility(View.GONE);
        }
    }

    // Helper method to update conversations
    public void updateConversations(List<ChatConversation> newConversations) {
        this.conversations = newConversations;
        notifyDataSetChanged();
    }

    // Helper method to add new conversation
    public void addConversation(ChatConversation conversation) {
        if (conversations != null) {
            conversations.add(0, conversation); // Add to top
            notifyItemInserted(0);
        }
    }

    // Helper method to update specific conversation
    public void updateConversation(ChatConversation updatedConversation) {
        if (conversations == null) return;
        
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getId().equals(updatedConversation.getId())) {
                conversations.set(i, updatedConversation);
                notifyItemChanged(i);
                break;
            }
        }
    }
} 