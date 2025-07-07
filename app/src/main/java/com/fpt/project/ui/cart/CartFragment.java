package com.fpt.project.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.MainActivity;
import com.fpt.project.R;
import com.fpt.project.data.model.Cart;
import com.fpt.project.data.model.CartItemResponse;
import com.fpt.project.data.repository.CartRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartItemAdapter.OnCartItemActionListener {

    private static final String TAG = "CartFragment";

    // Views
    private RecyclerView recyclerViewCart;
    private View layoutEmptyCart;
    private View layoutLoading;
    private View layoutCartSummary;
    private TextView tvCartCount;
    private TextView tvTotalAmount;
    private MaterialButton btnCheckout;
    private MaterialButton btnContinueShopping;

    // Data
    private CartItemAdapter cartAdapter;
    private List<CartItemResponse> cartItems;
    private CartRepository cartRepository;
    private Cart currentCart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadCart();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart when fragment becomes visible
        loadCart();
    }

    private void initViews(View view) {
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        layoutEmptyCart = view.findViewById(R.id.layoutEmptyCart);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutCartSummary = view.findViewById(R.id.layoutCartSummary);
        tvCartCount = view.findViewById(R.id.tvCartCount);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        btnContinueShopping = view.findViewById(R.id.btnContinueShopping);
        
        cartRepository = new CartRepository(getContext());
    }

    private void setupRecyclerView() {
        cartItems = new ArrayList<>();
        cartAdapter = new CartItemAdapter(cartItems, this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        btnContinueShopping.setOnClickListener(v -> {
            // Navigate to home tab
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        btnCheckout.setOnClickListener(v -> {
            // TODO: Implement checkout functionality
            Toast.makeText(getContext(), "Checkout coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCart() {
        Log.d(TAG, "Loading cart...");
        showLoading(true);

        cartRepository.getCart(new CartRepository.CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        currentCart = cart;
                        updateCartDisplay(cart);
                        
                        // Log cart status
                        if (cart == null) {
                            Log.d(TAG, "Cart is null (user hasn't added any items yet)");
                        } else {
                            Log.d(TAG, "Cart loaded successfully with " + cart.getTotalItems() + " items");
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Cart error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showEmptyCart(true);
                        Toast.makeText(getContext(), 
                            "Failed to load cart: " + message, 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Cart failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showEmptyCart(true);
                        Toast.makeText(getContext(), 
                            "Network error. Please check your connection.", 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void updateCartDisplay(Cart cart) {
        if (cart == null || cart.isEmpty()) {
            showEmptyCart(true);
            return;
        }

        Log.d(TAG, "Cart loaded - Total items: " + cart.getTotalItems() + 
              ", Total amount: $" + cart.getTotalAmount());

        // Update cart items
        cartItems.clear();
        cartItems.addAll(cart.getItems());
        cartAdapter.notifyDataSetChanged();

        // Update UI elements
        tvCartCount.setText(cart.getTotalItems() + " items");
        tvTotalAmount.setText(String.format("$%.2f", cart.getTotalAmount()));

        // Show/hide appropriate layouts
        showEmptyCart(false);
        layoutCartSummary.setVisibility(View.VISIBLE);
        recyclerViewCart.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewCart.setVisibility(show ? View.GONE : View.VISIBLE);
        layoutCartSummary.setVisibility(show ? View.GONE : 
            (currentCart != null && !currentCart.isEmpty() ? View.VISIBLE : View.GONE));
    }

    private void showEmptyCart(boolean show) {
        layoutEmptyCart.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewCart.setVisibility(show ? View.GONE : View.VISIBLE);
        layoutCartSummary.setVisibility(show ? View.GONE : View.VISIBLE);
        
        if (show) {
            tvCartCount.setText("0 items");
        }
    }

    // CartItemAdapter.OnCartItemActionListener implementation
    @Override
    public void onQuantityIncrease(CartItemResponse item) {
        Log.d(TAG, "Increase quantity for: " + item.getProductName() + " from " + item.getQuantity() + " to " + (item.getQuantity() + 1));
        
        int newQuantity = item.getQuantity() + 1;
        
        // Check stock limit
        if (item.getProduct() != null && newQuantity > item.getProduct().getStockQuantity()) {
            Toast.makeText(getContext(), 
                "Cannot add more. Only " + item.getProduct().getStockQuantity() + " items in stock.", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        updateCartItemQuantity(item.getProductId(), newQuantity);
    }

    @Override
    public void onQuantityDecrease(CartItemResponse item) {
        Log.d(TAG, "Decrease quantity for: " + item.getProductName() + " from " + item.getQuantity() + " to " + (item.getQuantity() - 1));
        
        int newQuantity = item.getQuantity() - 1;
        
        if (newQuantity < 1) {
            // If quantity becomes 0, remove the item instead
            removeCartItem(item);
            return;
        }
        
        updateCartItemQuantity(item.getProductId(), newQuantity);
    }

    @Override
    public void onRemoveItem(CartItemResponse item) {
        Log.d(TAG, "Remove item: " + item.getProductName());
        
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove \"" + item.getProductName() + "\" from your cart?")
                .setPositiveButton("Remove", (dialog, which) -> removeCartItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void updateCartItemQuantity(String productId, int newQuantity) {
        Log.d(TAG, "Updating cart item quantity - ProductID: " + productId + ", New quantity: " + newQuantity);
        
        // Show loading state (optional - you could add a progress indicator)
        Toast.makeText(getContext(), "Updating quantity...", Toast.LENGTH_SHORT).show();
        
        cartRepository.updateCartItem(productId, newQuantity, new CartRepository.CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(TAG, "Cart item quantity updated successfully");
                        currentCart = cart;
                        updateCartDisplay(cart);
                        Toast.makeText(getContext(), 
                            "Quantity updated successfully", 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Update quantity error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Failed to update quantity: " + message, 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Update quantity failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Network error. Please try again.", 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
    
    private void removeCartItem(CartItemResponse item) {
        Log.d(TAG, "Removing cart item - ProductID: " + item.getProductId());
        
        Toast.makeText(getContext(), "Removing " + item.getProductName() + "...", Toast.LENGTH_SHORT).show();
        
        cartRepository.removeCartItem(item.getProductId(), new CartRepository.CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(TAG, "Cart item removed successfully");
                        currentCart = cart;
                        updateCartDisplay(cart);
                        Toast.makeText(getContext(), 
                            item.getProductName() + " removed from cart", 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Remove item error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Failed to remove item: " + message, 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Remove item failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Network error. Please try again.", 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}
