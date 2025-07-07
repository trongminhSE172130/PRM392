package com.fpt.project.data.repository;

import android.content.Context;
import android.util.Log;

import com.fpt.project.data.model.Cart;
import com.fpt.project.data.model.CartItem;
import com.fpt.project.data.model.request.AddToCartRequest;
import com.fpt.project.data.model.request.UpdateCartRequest;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.response.CartResponse;
import com.fpt.project.data.network.ApiConfig;
import com.fpt.project.data.network.ApiService;
import com.fpt.project.data.network.NetworkClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    
    private static final String TAG = "CartRepository";
    private ApiService apiService;
    
    public CartRepository(Context context) {
        this.apiService = NetworkClient.getInstance(context).getApiService();
    }
    
    // Interface for callbacks
    public interface CartCallback<T> {
        void onSuccess(T data);
        void onError(String message);
        void onFailure(String error);
    }
    
    // Get cart (NEW API)
    public void getCart(CartCallback<Cart> callback) {
        Log.d(TAG, "Getting cart from API...");
        Log.d(TAG, "Cart endpoint: " + ApiConfig.USER_CART);
        Log.d(TAG, "Full URL will be: " + ApiConfig.BASE_URL + ApiConfig.USER_CART);
        
        Call<CartResponse> call = apiService.getCart();
        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                Log.d(TAG, "Cart API response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    
                    Log.d(TAG, "Get cart response - Success: " + cartResponse.isSuccess() + 
                          ", Message: " + cartResponse.getMessage());
                    
                    if (cartResponse.isSuccess()) {
                        Cart cart = cartResponse.getData();
                        if (cart != null) {
                            Log.d(TAG, "Cart loaded - Total items: " + cart.getTotalItems() + 
                                  ", Total amount: $" + cart.getTotalAmount());
                        }
                        callback.onSuccess(cart);
                    } else {
                        callback.onError(cartResponse.getMessage());
                    }
                } else if (response.code() == 404) {
                    // User doesn't have a cart yet (empty cart)
                    Log.d(TAG, "No cart found (404) - treating as empty cart");
                    callback.onSuccess(null); // Return null to indicate empty cart
                } else {
                    String errorMessage = "Failed to get cart: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMessage += " - " + errorBody;
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Log.e(TAG, errorMessage);
                    
                    // For 401/403, might be auth issue
                    if (response.code() == 401 || response.code() == 403) {
                        callback.onError("Authentication required. Please login again.");
                    } else {
                        callback.onError(errorMessage);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "Get cart failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Get all cart items (Legacy)
    public void getCartItems(CartCallback<List<CartItem>> callback) {
        Call<ApiResponse<List<CartItem>>> call = apiService.getCartItems();
        call.enqueue(new Callback<ApiResponse<List<CartItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CartItem>>> call, Response<ApiResponse<List<CartItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<CartItem>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get cart items: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<CartItem>>> call, Throwable t) {
                Log.e(TAG, "Get cart items failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Add item to cart - NEW API
    public void addToCart(AddToCartRequest request, CartCallback<Cart> callback) {
        Log.d(TAG, "Adding to cart - Product ID: " + request.getProductId() + 
              ", Quantity: " + request.getQuantity());
        
        Call<CartResponse> call = apiService.addToCart(request);
        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    
                    Log.d(TAG, "Add to cart response - Success: " + cartResponse.isSuccess() + 
                          ", Message: " + cartResponse.getMessage());
                    
                    if (cartResponse.isSuccess()) {
                        Cart cart = cartResponse.getData();
                        if (cart != null) {
                            Log.d(TAG, "Cart updated - Total items: " + cart.getTotalItems() + 
                                  ", Total amount: $" + cart.getTotalAmount());
                        }
                        callback.onSuccess(cart);
                    } else {
                        callback.onError(cartResponse.getMessage());
                    }
                } else {
                    String errorMessage = "Failed to add item to cart: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "Add to cart failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Add item to cart - Legacy method for backward compatibility
    public void addToCart(CartItem cartItem, CartCallback<CartItem> callback) {
        // Convert CartItem to AddToCartRequest
        AddToCartRequest request = new AddToCartRequest(
            cartItem.getProductId(),
            cartItem.getQuantity()
        );
        
        // Call new API and convert response
        addToCart(request, new CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                // Convert Cart back to CartItem for compatibility
                if (cart != null && !cart.getItems().isEmpty()) {
                    // Return the first item (typically the one just added)
                    com.fpt.project.data.model.CartItemResponse cartItemResponse = cart.getItems().get(0);
                    
                    // Convert to legacy CartItem
                    CartItem resultItem = new CartItem();
                    resultItem.setProductId(cartItemResponse.getProductId());
                    resultItem.setQuantity(cartItemResponse.getQuantity());
                    resultItem.setProduct(cartItemResponse.getProduct());
                    
                    callback.onSuccess(resultItem);
                } else {
                    callback.onError("Cart data is empty");
                }
            }
            
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
            
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }
    
    // Update cart item quantity (NEW API)
    public void updateCartItem(String productId, int quantity, CartCallback<Cart> callback) {
        Log.d(TAG, "Updating cart item - ProductID: " + productId + ", Quantity: " + quantity);
        
        UpdateCartRequest request = new UpdateCartRequest(productId, quantity);
        Log.d(TAG, "Update request: " + request.toString());
        
        Call<CartResponse> call = apiService.updateCartItem(request);
        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                Log.d(TAG, "Update cart response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    
                    Log.d(TAG, "Update cart response - Success: " + cartResponse.isSuccess() + 
                          ", Message: " + cartResponse.getMessage());
                    
                    if (cartResponse.isSuccess()) {
                        Cart cart = cartResponse.getData();
                        if (cart != null) {
                            Log.d(TAG, "Cart updated - Total items: " + cart.getTotalItems() + 
                                  ", Total amount: $" + cart.getTotalAmount());
                        }
                        callback.onSuccess(cart);
                    } else {
                        callback.onError(cartResponse.getMessage());
                    }
                } else {
                    String errorMessage = "Failed to update cart: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Update error body: " + errorBody);
                            errorMessage += " - " + errorBody;
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "Update cart failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Remove item from cart (NEW API)
    public void removeCartItem(String productId, CartCallback<Cart> callback) {
        Log.d(TAG, "Removing cart item - ProductID: " + productId);
        
        Call<CartResponse> call = apiService.removeCartItem(productId);
        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                Log.d(TAG, "Remove cart response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    
                    Log.d(TAG, "Remove cart response - Success: " + cartResponse.isSuccess() + 
                          ", Message: " + cartResponse.getMessage());
                    
                    if (cartResponse.isSuccess()) {
                        Cart cart = cartResponse.getData();
                        if (cart != null) {
                            Log.d(TAG, "Item removed - Total items: " + cart.getTotalItems() + 
                                  ", Total amount: $" + cart.getTotalAmount());
                        } else {
                            Log.d(TAG, "Cart is now empty after removing item");
                        }
                        callback.onSuccess(cart);
                    } else {
                        callback.onError(cartResponse.getMessage());
                    }
                } else if (response.code() == 404) {
                    // Item not found or cart is empty
                    Log.d(TAG, "Item not found (404) - cart might be empty");
                    callback.onSuccess(null); // Return null to indicate empty cart
                } else {
                    String errorMessage = "Failed to remove item: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Remove error body: " + errorBody);
                            errorMessage += " - " + errorBody;
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "Remove cart failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Clear entire cart
    public void clearCart(CartCallback<Object> callback) {
        Call<ApiResponse<Object>> call = apiService.clearCart();
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to clear cart: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Clear cart failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Get cart item count
    public void getCartItemCount(CartCallback<Object> callback) {
        Call<ApiResponse<Object>> call = apiService.getCartItemCount();
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get cart count: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Get cart count failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Validate cart before checkout
    public void validateCart(CartCallback<Object> callback) {
        Call<ApiResponse<Object>> call = apiService.validateCart();
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to validate cart: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Validate cart failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
} 