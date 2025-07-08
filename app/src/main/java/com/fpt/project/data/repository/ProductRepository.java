package com.fpt.project.data.repository;

import android.content.Context;
import android.util.Log;

import com.fpt.project.data.model.Product;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.response.ProductResponse;
import com.fpt.project.data.network.ApiService;
import com.fpt.project.data.network.NetworkClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    
    private static final String TAG = "ProductRepository";
    private ApiService apiService;
    
    public ProductRepository(Context context) {
        this.apiService = NetworkClient.getInstance(context).getApiService();
    }
    
    // Interface for callbacks
    public interface ProductCallback<T> {
        void onSuccess(T data);
        void onError(String message);
        void onFailure(String error);
    }
    
    // Interface for product list with pagination
    public interface ProductListCallback {
        void onSuccess(List<Product> products, ProductResponse.Pagination pagination);
        void onError(String message);
        void onFailure(String error);
    }
    
    // Get all products with pagination
    public void getProducts(int page, int limit, String search, ProductListCallback callback) {
        Call<ProductResponse> call = apiService.getProducts(page, limit, search);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.body() != null) {
                    ProductResponse productResponse = response.body();
                    
                    // Check response body success field instead of HTTP status code
                    if (productResponse.isSuccess()) {
                        Log.d(TAG, "Products loaded successfully: " + productResponse.getData().size() + " items");
                        callback.onSuccess(productResponse.getData(), productResponse.getPagination());
                    } else {
                        callback.onError("Failed to get products");
                    }
                } else if (response.isSuccessful()) {
                    callback.onError("Failed to get products: Empty response");
                } else {
                    callback.onError("Failed to get products: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e(TAG, "Get products failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Get products with category filter using /api/products?category=
    public void getProductsByCategory(int page, int limit, String categoryId, ProductListCallback callback) {
        // Use /api/products with category parameter
        Call<ProductResponse> call = apiService.getProducts(page, limit, null, categoryId);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.body() != null) {
                    ProductResponse productResponse = response.body();
                    
                    // Check response body success field instead of HTTP status code
                    if (productResponse.isSuccess()) {
                        Log.d(TAG, "Products by category loaded successfully: " + productResponse.getData().size() + " items");
                        callback.onSuccess(productResponse.getData(), productResponse.getPagination());
                    } else {
                        callback.onError("Failed to get products by category");
                    }
                } else if (response.isSuccessful()) {
                    callback.onError("Failed to get products by category: Empty response");
                } else {
                    callback.onError("Failed to get products by category: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e(TAG, "Get products by category failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Get all products (simple callback without pagination) 
    public void getProducts(int page, int limit, String search, ProductCallback<List<Product>> callback) {
        getProducts(page, limit, search, new ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, ProductResponse.Pagination pagination) {
                callback.onSuccess(products);
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
    
    // Get product detail by ID
    public void getProductDetail(int productId, ProductCallback<Product> callback) {
        Call<ApiResponse<Product>> call = apiService.getProductDetail(productId);
        call.enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get product detail: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Log.e(TAG, "Get product detail failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Get products by category (simple callback - legacy method)
    public void getProductsByCategory(String categoryId, int page, int limit, ProductCallback<List<Product>> callback) {
        // Use the new method with ProductListCallback and convert to simple callback
        getProductsByCategory(page, limit, categoryId, new ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, ProductResponse.Pagination pagination) {
                callback.onSuccess(products);
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
    
    // Get featured products
    public void getFeaturedProducts(ProductCallback<List<Product>> callback) {
        Call<ApiResponse<List<Product>>> call = apiService.getFeaturedProducts();
        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get featured products: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Log.e(TAG, "Get featured products failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Note: Category methods moved to CategoryRepository
} 