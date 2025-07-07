package com.fpt.project.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.CartItemResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<CartItemResponse> cartItems;
    private OnCartItemActionListener actionListener;

    public interface OnCartItemActionListener {
        void onQuantityIncrease(CartItemResponse item);
        void onQuantityDecrease(CartItemResponse item);
        void onRemoveItem(CartItemResponse item);
    }

    public CartItemAdapter(List<CartItemResponse> cartItems, OnCartItemActionListener listener) {
        this.cartItems = cartItems;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItemResponse item = cartItems.get(position);
        holder.bind(item, actionListener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateItems(List<CartItemResponse> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCartProductImage;
        private TextView tvCartProductName;
        private TextView tvCartProductPrice;
        private TextView tvCartQuantity;
        private TextView tvCartItemTotal;
        private MaterialButton btnDecreaseQuantity;
        private MaterialButton btnIncreaseQuantity;
        private MaterialButton btnRemoveItem;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartProductImage = itemView.findViewById(R.id.ivCartProductImage);
            tvCartProductName = itemView.findViewById(R.id.tvCartProductName);
            tvCartProductPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvCartItemTotal = itemView.findViewById(R.id.tvCartItemTotal);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }

        public void bind(CartItemResponse item, OnCartItemActionListener listener) {
            // Product info
            tvCartProductName.setText(item.getProductName());
            tvCartProductPrice.setText(String.format("$%.2f", item.getPrice()));
            tvCartQuantity.setText(String.valueOf(item.getQuantity()));
            tvCartItemTotal.setText(String.format("$%.2f", item.getTotal()));

            // Load product image
            String imageUrl = item.getProductImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // TODO: Load image using Glide library
                // For now, use placeholder
                ivCartProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
            } else {
                ivCartProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Update button states
            updateQuantityButtons(item);

            // Click listeners
            btnIncreaseQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityIncrease(item);
                }
            });

            btnDecreaseQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityDecrease(item);
                }
            });

            btnRemoveItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(item);
                }
            });
        }

        private void updateQuantityButtons(CartItemResponse item) {
            // Enable/disable decrease button based on quantity
            btnDecreaseQuantity.setEnabled(item.getQuantity() > 1);
            
            // Check stock limit for increase button
            if (item.getProduct() != null) {
                boolean canIncrease = item.getQuantity() < item.getProduct().getStockQuantity();
                btnIncreaseQuantity.setEnabled(canIncrease && item.getProduct().isActive());
            } else {
                btnIncreaseQuantity.setEnabled(true); // Default to enabled if no product info
            }
        }
    }
} 