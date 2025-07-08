package com.fpt.project.ui.cart;

import android.content.Intent;
import android.net.Uri;
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
import com.fpt.project.data.model.Order;
import com.fpt.project.data.model.request.CheckoutRequest;
import com.fpt.project.data.model.response.PaymentResponse;
import com.fpt.project.data.repository.CartRepository;
import com.google.android.material.button.MaterialButton;
import android.net.Uri;

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
            showCheckoutDialog();
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
    
    // ================ CHECKOUT FUNCTIONALITY ================
    
    private void showCheckoutDialog() {
        if (currentCart == null || currentCart.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        CheckoutDialog checkoutDialog = new CheckoutDialog(getContext(), new CheckoutDialog.CheckoutDialogListener() {
            @Override
            public void onCheckoutConfirmed(CheckoutRequest checkoutRequest) {
                processCheckout(checkoutRequest);
            }
            
            @Override
            public void onCheckoutCancelled() {
                Log.d(TAG, "Checkout cancelled by user");
            }
        });
        
        checkoutDialog.show();
    }
    
    private void processCheckout(CheckoutRequest checkoutRequest) {
        Log.d(TAG, "Processing checkout for payment method: " + checkoutRequest.getPayment_method());
        
        // Show loading
        Toast.makeText(getContext(), "Processing your order...", Toast.LENGTH_SHORT).show();
        
        cartRepository.checkout(checkoutRequest, new CartRepository.CartCallback<Order>() {
            @Override
            public void onSuccess(Order order) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (order == null) {
                            Log.e(TAG, "Order object is null");
                            Toast.makeText(getContext(), "Order creation failed - no order data", Toast.LENGTH_LONG).show();
                            return;
                        }
                        
                        String orderId = order.getId();
                        String orderCode = order.getOrderCode();
                        Log.d(TAG, "Order created successfully - Order ID: " + orderId + ", Order Code: " + orderCode);
                        Log.d(TAG, "Order details - Status: " + order.getStatus() + ", Total: " + order.getTotalAmount() + ", Payment: " + order.getPaymentMethod());
                        
                        if (orderId == null || orderId.isEmpty()) {
                            Log.e(TAG, "Order ID is null or empty");
                            Toast.makeText(getContext(), "Order creation failed - missing order ID", Toast.LENGTH_LONG).show();
                            return;
                        }
                        
                        // Check payment method
                        if ("vnpay".equals(checkoutRequest.getPayment_method())) {
                            // Process VNPay payment
                            processVnpayPayment(orderId);
                        } else {
                            // COD - just show success message
                            showCheckoutSuccess(order);
                        }
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                Log.e(TAG, "Checkout error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Checkout failed: " + message, 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Checkout failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Network error during checkout. Please try again.", 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
    
    private void processVnpayPayment(String orderId) {
        Log.d(TAG, "Processing VNPay payment for order: " + orderId);
        
        Toast.makeText(getContext(), "Creating payment link...", Toast.LENGTH_SHORT).show();
        
        // Create return URL for VNPay to redirect back to our app
        String returnUrl = "prm392app://payment/result?order_id=" + orderId;
        
        cartRepository.createPayment(orderId, returnUrl, new CartRepository.CartCallback<PaymentResponse.PaymentData>() {
            @Override
            public void onSuccess(PaymentResponse.PaymentData paymentData) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (paymentData != null && paymentData.getPayment_url() != null) {
                            Log.d(TAG, "✅ VNPay payment URL created successfully");
                            Log.d(TAG, "Payment URL: " + paymentData.getPayment_url());
                            Log.d(TAG, "Order ID: " + orderId);
                            
                            // Validate URL before opening
                            if (paymentData.getPayment_url().startsWith("https://") && 
                                paymentData.getPayment_url().contains("vnpay")) {
                                Log.d(TAG, "✅ URL validation passed, opening payment in browser");
                                
                                // Open VNPay payment in external browser (ONLY method)
                                openVnpayPaymentInBrowser(paymentData.getPayment_url(), orderId);
                                
                            } else {
                                Log.e(TAG, "❌ Invalid payment URL format: " + paymentData.getPayment_url());
                                Toast.makeText(getContext(), "Invalid payment URL received", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e(TAG, "❌ Payment URL is null or empty");
                            Toast.makeText(getContext(), "Failed to create payment URL", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                Log.e(TAG, "Payment creation error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Failed to create payment: " + message, 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Payment creation failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Network error during payment creation. Please try again.", 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
    
    // Open VNPay payment URL in external browser (ONLY method)
    private void openVnpayPaymentInBrowser(String paymentUrl, String orderId) {
        try {
            Log.d(TAG, "🌐 Opening VNPay payment in browser: " + paymentUrl);
            
            // Create intent for opening URL
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            
            // Check what activities can handle this intent
            List<android.content.pm.ResolveInfo> activities = getContext().getPackageManager()
                .queryIntentActivities(browserIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
            
            if (activities != null && !activities.isEmpty()) {
                Log.d(TAG, "✅ Found " + activities.size() + " browser(s) available");
                for (android.content.pm.ResolveInfo activity : activities) {
                    Log.d(TAG, "Available browser: " + activity.activityInfo.packageName);
                }
                
                // Just start the intent - Android will show chooser or use default
                startActivity(browserIntent);
                showPaymentMessage(orderId);
                loadCart();
                return;
            }
            
            // Try Chrome explicitly as backup
            Log.w(TAG, "⚠️ No default browser found, trying Chrome explicitly");
            Intent chromeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            chromeIntent.setPackage("com.android.chrome");
            
            try {
                startActivity(chromeIntent);
                Log.d(TAG, "✅ Chrome opened successfully");
                showPaymentMessage(orderId);
                loadCart();
                return;
            } catch (android.content.ActivityNotFoundException e) {
                Log.e(TAG, "Chrome not found: " + e.getMessage());
            }
            
            // Try any browser that can handle HTTP URLs (last resort)
            Log.w(TAG, "⚠️ Trying any app that can handle HTTP URLs");
            Intent anyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"));
            List<android.content.pm.ResolveInfo> httpActivities = getContext().getPackageManager()
                .queryIntentActivities(anyIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
                
            if (httpActivities != null && !httpActivities.isEmpty()) {
                Log.d(TAG, "✅ Found HTTP handler, redirecting to VNPay URL");
                // Create new intent with VNPay URL
                Intent httpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                startActivity(httpIntent);
                showPaymentMessage(orderId);
                loadCart();
                return;
            }
            
            // No browser available at all
            Log.e(TAG, "❌ No browser available for VNPay payment");
            Toast.makeText(getContext(), 
                "No browser app found.\n\n" +
                "Please install Chrome, Firefox, or another browser to complete payment.\n\n" +
                "You can also copy the payment URL manually.", 
                Toast.LENGTH_LONG).show();
            
            // Offer to copy URL to clipboard as last resort
            copyUrlToClipboard(paymentUrl);
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to open VNPay payment in browser", e);
            Toast.makeText(getContext(), 
                "Failed to open payment page: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
                
            // Offer to copy URL
            copyUrlToClipboard(paymentUrl);
        }
    }
    
    private void copyUrlToClipboard(String url) {
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) 
                getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("VNPay Payment URL", url);
            clipboard.setPrimaryClip(clip);
            
            Toast.makeText(getContext(), 
                "💾 Payment URL copied to clipboard!\n\nYou can paste it in any browser to complete payment.", 
                Toast.LENGTH_LONG).show();
                
        } catch (Exception e) {
            Log.e(TAG, "Failed to copy URL to clipboard", e);
        }
    }
    
    private void showPaymentMessage(String orderId) {
        Toast.makeText(getContext(), 
            "🌐 Opening VNPay payment in browser...\n" +
            "Order: " + orderId + "\n\n" +
            "✅ You will return to app after payment", 
            Toast.LENGTH_LONG).show();
    }
    
    private void showCheckoutSuccess(Order order) {
        String displayOrderId = order.getOrderCode() != null ? order.getOrderCode() : order.getId();
        
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Order Placed Successfully!")
                .setMessage("Your order #" + displayOrderId + " has been placed successfully.\n\n" +
                           "Total: $" + String.format("%.2f", order.getTotalAmount()) + "\n" +
                           "Payment Method: " + order.getPaymentMethod().toUpperCase() + "\n\n" +
                           "You will receive a confirmation shortly.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Refresh cart and navigate to home
                    loadCart();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateToHome();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
