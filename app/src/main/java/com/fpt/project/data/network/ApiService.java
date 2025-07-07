package com.fpt.project.data.network;

import com.fpt.project.data.model.CartItem;
import com.fpt.project.data.model.ChatMessage;
import com.fpt.project.data.model.Order;
import com.fpt.project.data.model.Product;
import com.fpt.project.data.model.StoreInfo;
import com.fpt.project.data.model.User;
import com.fpt.project.data.model.request.LoginRequest;
import com.fpt.project.data.model.request.RegisterRequest;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.response.LoginResponse;
import com.fpt.project.data.model.response.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // ======================== AUTHENTICATION APIs ========================
    
    @POST(ApiConfig.AUTH_LOGIN)
    Call<LoginResponse> login(@Body LoginRequest request);
    
    @POST(ApiConfig.AUTH_REGISTER)
    Call<RegisterResponse> register(@Body RegisterRequest request);
    
    @POST(ApiConfig.AUTH_LOGOUT)
    Call<ApiResponse<Object>> logout();
    
    @GET(ApiConfig.AUTH_PROFILE)
    Call<ApiResponse<User>> getProfile();
    
    @PUT(ApiConfig.AUTH_PROFILE)
    Call<ApiResponse<User>> updateProfile(@Body User user);
    
    // ======================== PRODUCT APIs ========================
    
    @GET(ApiConfig.PRODUCTS)
    Call<ApiResponse<List<Product>>> getProducts(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("search") String search
    );
    
    @POST(ApiConfig.PRODUCTS)
    Call<ApiResponse<Product>> createProduct(@Body Product product);
    
    @GET(ApiConfig.PRODUCTS_FEATURED)
    Call<ApiResponse<List<Product>>> getFeaturedProducts();
    
    @GET(ApiConfig.PRODUCT_DETAIL)
    Call<ApiResponse<Product>> getProductDetail(@Path("id") int productId);
    
    @PUT(ApiConfig.PRODUCT_UPDATE)
    Call<ApiResponse<Product>> updateProduct(@Path("id") int productId, @Body Product product);
    
    @DELETE(ApiConfig.PRODUCT_DELETE)
    Call<ApiResponse<Object>> deleteProduct(@Path("id") int productId);
    
    @DELETE(ApiConfig.PRODUCT_DELETE_PERMANENT)
    Call<ApiResponse<Object>> deleteProductPermanent(@Path("id") int productId);
    
    @PATCH(ApiConfig.PRODUCT_UPDATE_STOCK)
    Call<ApiResponse<Product>> updateProductStock(@Path("id") int productId, @Body Object stockUpdate);
    
    @PATCH(ApiConfig.PRODUCT_TOGGLE_FEATURED)
    Call<ApiResponse<Product>> toggleProductFeatured(@Path("id") int productId);
    
    @GET(ApiConfig.PRODUCTS_BY_CATEGORY)
    Call<ApiResponse<List<Product>>> getProductsByCategory(
            @Path("categoryId") int categoryId,
            @Query("page") int page,
            @Query("limit") int limit
    );
    
    // ======================== CATEGORY APIs ========================
    
    @GET(ApiConfig.CATEGORIES)
    Call<ApiResponse<List<Object>>> getCategories();
    
    @POST(ApiConfig.CATEGORIES)
    Call<ApiResponse<Object>> createCategory(@Body Object category);
    
    @GET(ApiConfig.CATEGORIES_ACTIVE)
    Call<ApiResponse<List<Object>>> getActiveCategories();
    
    @GET(ApiConfig.CATEGORY_DETAIL)
    Call<ApiResponse<Object>> getCategoryDetail(@Path("id") int categoryId);
    
    @PUT(ApiConfig.CATEGORY_UPDATE)
    Call<ApiResponse<Object>> updateCategory(@Path("id") int categoryId, @Body Object category);
    
    @DELETE(ApiConfig.CATEGORY_DELETE)
    Call<ApiResponse<Object>> deleteCategory(@Path("id") int categoryId);
    
    @DELETE(ApiConfig.CATEGORY_DELETE_PERMANENT)
    Call<ApiResponse<Object>> deleteCategoryPermanent(@Path("id") int categoryId);
    
    // ======================== CART APIs ========================
    
    @GET(ApiConfig.CART)
    Call<ApiResponse<List<CartItem>>> getCartItems();
    
    @POST(ApiConfig.CART_ADD)
    Call<ApiResponse<CartItem>> addToCart(@Body CartItem cartItem);
    
    @PUT(ApiConfig.CART_UPDATE)
    Call<ApiResponse<CartItem>> updateCartItem(@Body CartItem cartItem);
    
    @DELETE(ApiConfig.CART_REMOVE)
    Call<ApiResponse<Object>> removeCartItem(@Body Object removeRequest);
    
    @DELETE(ApiConfig.CART_CLEAR)
    Call<ApiResponse<Object>> clearCart();
    
    @GET(ApiConfig.CART_COUNT)
    Call<ApiResponse<Object>> getCartItemCount();
    
    @GET(ApiConfig.CART_VALIDATE)
    Call<ApiResponse<Object>> validateCart();
    
    // ======================== ORDER APIs ========================
    
    @POST(ApiConfig.ORDERS)
    Call<ApiResponse<Order>> createOrder(@Body Order order);
    
    @GET(ApiConfig.ORDERS)
    Call<ApiResponse<List<Order>>> getOrders(
            @Query("page") int page,
            @Query("limit") int limit
    );
    
    @GET(ApiConfig.ORDER_DETAIL)
    Call<ApiResponse<Order>> getOrderDetail(@Path("id") int orderId);
    
    @PUT(ApiConfig.ORDER_STATUS)
    Call<ApiResponse<Order>> updateOrderStatus(
            @Path("id") int orderId,
            @Body Object statusUpdate
    );
    
    // ======================== CHAT APIs ========================
    
    @GET(ApiConfig.CHAT_MESSAGES)
    Call<ApiResponse<List<ChatMessage>>> getChatMessages(
            @Query("page") int page,
            @Query("limit") int limit
    );
    
    @POST(ApiConfig.CHAT_MESSAGES)
    Call<ApiResponse<ChatMessage>> sendMessage(@Body ChatMessage message);
    
    @PUT(ApiConfig.CHAT_MESSAGE_READ)
    Call<ApiResponse<Object>> markMessageAsRead(@Path("id") int messageId);
    
    // ======================== STORE APIs ========================
    
    @GET(ApiConfig.STORE_INFO)
    Call<ApiResponse<StoreInfo>> getStoreInfo();
    
    @GET(ApiConfig.STORE_LOCATION)
    Call<ApiResponse<StoreInfo>> getStoreLocation();
} 