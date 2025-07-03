package com.fpt.project.data.repository;

import android.content.Context;
import android.util.Log;

import com.fpt.project.data.model.CartItem;
import com.fpt.project.data.model.response.ApiResponse;
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
    
    // Get all cart items
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
    
    // Add item to cart
    public void addToCart(CartItem cartItem, CartCallback<CartItem> callback) {
        Call<ApiResponse<CartItem>> call = apiService.addToCart(cartItem);
        call.enqueue(new Callback<ApiResponse<CartItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartItem>> call, Response<ApiResponse<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartItem> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to add item to cart: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<CartItem>> call, Throwable t) {
                Log.e(TAG, "Add to cart failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Update cart item quantity
    public void updateCartItem(CartItem cartItem, CartCallback<CartItem> callback) {
        Call<ApiResponse<CartItem>> call = apiService.updateCartItem(cartItem);
        call.enqueue(new Callback<ApiResponse<CartItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartItem>> call, Response<ApiResponse<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartItem> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to update cart item: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<CartItem>> call, Throwable t) {
                Log.e(TAG, "Update cart item failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Remove item from cart
    public void removeCartItem(Object removeRequest, CartCallback<Object> callback) {
        Call<ApiResponse<Object>> call = apiService.removeCartItem(removeRequest);
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
                    callback.onError("Failed to remove cart item: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Remove cart item failure: " + t.getMessage());
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