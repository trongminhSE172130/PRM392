package com.fpt.project.data.network;

import com.fpt.project.data.model.CartItem;
import com.fpt.project.data.model.ChatMessage;
import com.fpt.project.data.model.Order;
import com.fpt.project.data.model.Product;
import com.fpt.project.data.model.StoreInfo;
import com.fpt.project.data.model.User;
import com.fpt.project.data.model.request.LoginRequest;
import com.fpt.project.data.model.request.RegisterRequest;
import com.fpt.project.data.model.request.AddToCartRequest;
import com.fpt.project.data.model.request.UpdateCartRequest;
import com.fpt.project.data.model.request.CheckoutRequest;
import com.fpt.project.data.model.request.PaymentRequest;
import com.fpt.project.data.model.request.SendMessageRequest;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.response.CartResponse;
import com.fpt.project.data.model.response.LoginResponse;
import com.fpt.project.data.model.response.ProductResponse;
import com.fpt.project.data.model.response.RegisterResponse;
import com.fpt.project.data.model.response.CheckoutResponse;
import com.fpt.project.data.model.response.PaymentResponse;
import com.fpt.project.data.model.response.CategoryResponse;
import com.fpt.project.data.model.response.MessagesResponse;
import com.fpt.project.data.model.response.SendMessageResponse;

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
    Call<ProductResponse> getProducts(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("search") String search
    );
    
    @GET(ApiConfig.PRODUCTS)
    Call<ProductResponse> getProducts(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("search") String search,
            @Query("category") String categoryId
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
    Call<ProductResponse> getProductsByCategory(
            @Path("categoryId") String categoryId,
            @Query("page") int page,
            @Query("limit") int limit
    );
    
    // ======================== CATEGORY APIs ========================
    
    @GET(ApiConfig.CATEGORIES)
    Call<CategoryResponse> getCategories();
    
    @POST(ApiConfig.CATEGORIES)
    Call<ApiResponse<Object>> createCategory(@Body Object category);
    
    @GET(ApiConfig.CATEGORY_DETAIL)
    Call<ApiResponse<Object>> getCategoryDetail(@Path("id") int categoryId);
    
    @PUT(ApiConfig.CATEGORY_UPDATE)
    Call<ApiResponse<Object>> updateCategory(@Path("id") int categoryId, @Body Object category);
    
    @DELETE(ApiConfig.CATEGORY_DELETE)
    Call<ApiResponse<Object>> deleteCategory(@Path("id") int categoryId);
    
    @DELETE(ApiConfig.CATEGORY_DELETE_PERMANENT)
    Call<ApiResponse<Object>> deleteCategoryPermanent(@Path("id") int categoryId);
    
    // ======================== CART APIs ========================
    
    @GET(ApiConfig.USER_CART)
    Call<CartResponse> getCart();
    
    @GET(ApiConfig.USER_CART)
    Call<ApiResponse<List<CartItem>>> getCartItems();
    
    @POST(ApiConfig.CART_ADD)
    Call<CartResponse> addToCart(@Body AddToCartRequest request);
    
    @PUT(ApiConfig.CART_UPDATE)
    Call<CartResponse> updateCartItem(@Body UpdateCartRequest request);
    
    @DELETE("cart/remove/{productId}")
    Call<CartResponse> removeCartItem(@Path("productId") String productId);
    
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
    
    @POST("orders/checkout")
    Call<CheckoutResponse> checkout(@Body CheckoutRequest request);
    
    @POST("orders/{id}/payment")
    Call<PaymentResponse> createPayment(@Path("id") String orderId, @Body PaymentRequest paymentRequest);
    
    // ======================== CHAT APIs ========================
    
    // Get user's conversations
    @GET(ApiConfig.CONVERSATION_BY_USER)
    Call<ApiResponse<List<Object>>> getMyConversations();
 
    // Create new conversation with staff
    @POST(ApiConfig.CONVERSATION)
    Call<ApiResponse<Object>> createConversation(@Body Object conversationRequest);
    
    // Get conversation detail
    @GET(ApiConfig.CONVERSATION_DETAIL)
    Call<ApiResponse<Object>> getConversationDetail(@Path("id") String conversationId);
    
    // Get messages in conversation
    @GET(ApiConfig.MESSAGE_IN_CONVERSATION)
    Call<ApiResponse<MessagesResponse>> getChatMessages(
            @Path("id") String conversationId,
            @Query("page") int page,
            @Query("limit") int limit
    );
    
    // Send message in conversation
    @POST(ApiConfig.SEND_MESSAGE)
    Call<SendMessageResponse> sendMessage(
            @Path("id") String conversationId, 
            @Body SendMessageRequest request
    );
    
    // ======================== STORE APIs ========================
    
    @GET(ApiConfig.STORE_INFO)
    Call<ApiResponse<StoreInfo>> getStoreInfo();
    
    @GET(ApiConfig.STORE_LOCATION)
    Call<ApiResponse<StoreInfo>> getStoreLocation();
} 