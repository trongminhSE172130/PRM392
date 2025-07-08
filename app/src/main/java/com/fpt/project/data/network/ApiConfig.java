package com.fpt.project.data.network;

public class ApiConfig {
    
    // Base URL for API calls
    // For local development (Android Emulator)
    public static final String BASE_URL = "http://10.0.2.2:5000/api/";
    
    // Alternative URLs for different environments
    // public static final String BASE_URL = "http://192.168.1.100:5000/api/"; // For real device on same network
    // public static final String BASE_URL = "https://your-api-domain.com/api/"; // For production

    // API Endpoints - Updated to match your actual API
    
    // Authentication APIs (you may need to implement these)
    public static final String AUTH_LOGIN = "auth/login";
    public static final String AUTH_REGISTER = "auth/register";
    public static final String AUTH_LOGOUT = "auth/logout";
    public static final String AUTH_PROFILE = "auth/me";
    
    // Product APIs
    public static final String PRODUCTS = "products";
    public static final String PRODUCTS_FEATURED = "products/featured";
    public static final String PRODUCT_DETAIL = "products/{id}";
    public static final String PRODUCT_UPDATE = "products/{id}";
    public static final String PRODUCT_DELETE = "products/{id}";
    public static final String PRODUCT_DELETE_PERMANENT = "products/{id}/permanent";
    public static final String PRODUCT_UPDATE_STOCK = "products/{id}/stock";
    public static final String PRODUCT_TOGGLE_FEATURED = "products/{id}/featured";
    public static final String PRODUCTS_BY_CATEGORY = "categories/{categoryId}/products";
    
    // Category APIs
    public static final String CATEGORIES = "categories";
    public static final String CATEGORY_DETAIL = "categories/{id}";
    public static final String CATEGORY_UPDATE = "categories/{id}";
    public static final String CATEGORY_DELETE = "categories/{id}";
    public static final String CATEGORY_DELETE_PERMANENT = "categories/{id}/permanent";
    
    // Cart APIs
    public static final String USER_CART = "cart";
    public static final String CART_COUNT = "cart/count";
    public static final String CART_ADD = "cart/add";
    public static final String CART_UPDATE = "cart/update";
    public static final String CART_REMOVE = "cart/remove/{id}";
    public static final String CART_CLEAR = "cart/clear";
    public static final String CART_VALIDATE = "cart/validate";
    
    // Order APIs (you may need to implement these)
    public static final String ORDERS = "orders";
    public static final String ORDER_DETAIL = "orders/{id}";
    public static final String ORDER_STATUS = "orders/{id}/status"; //admin
    public static final String CHECKOUT = "orders/checkout";
    public static final String PAYMENT = "orders/{id}/payment";
    public static final String CANCEL_ORDER = "orders/{id}/cancel";
    public static final String GET_ORDERS = "orders/admin/all"; //admin
    // Chat APIs (you may need to implement these)
    public static final String CHAT_MESSAGES = "chat/messages";
    public static final String CHAT_MESSAGE_READ = "chat/messages/{id}/read";
    
    // Store APIs (you may need to implement these)
    public static final String STORE_INFO = "store/info";
    public static final String STORE_LOCATION = "store/location";
    
    // Request timeouts (in seconds)
    public static final int CONNECTION_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
} 