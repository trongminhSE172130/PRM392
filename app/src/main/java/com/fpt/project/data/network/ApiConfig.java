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
    public static final String AUTH_PROFILE = "auth/profile";
    
    // Product APIs
    public static final String PRODUCTS = "ecommerce/products";
    public static final String PRODUCTS_FEATURED = "ecommerce/products/featured";
    public static final String PRODUCT_DETAIL = "ecommerce/products/{id}";
    public static final String PRODUCT_UPDATE = "ecommerce/products/{id}";
    public static final String PRODUCT_DELETE = "ecommerce/products/{id}";
    public static final String PRODUCT_DELETE_PERMANENT = "ecommerce/products/{id}/permanent";
    public static final String PRODUCT_UPDATE_STOCK = "ecommerce/products/{id}/stock";
    public static final String PRODUCT_TOGGLE_FEATURED = "ecommerce/products/{id}/featured";
    public static final String PRODUCTS_BY_CATEGORY = "ecommerce/categories/{categoryId}/products";
    
    // Category APIs
    public static final String CATEGORIES = "ecommerce/categories";
    public static final String CATEGORIES_ACTIVE = "ecommerce/categories/active";
    public static final String CATEGORY_DETAIL = "ecommerce/categories/{id}";
    public static final String CATEGORY_UPDATE = "ecommerce/categories/{id}";
    public static final String CATEGORY_DELETE = "ecommerce/categories/{id}";
    public static final String CATEGORY_DELETE_PERMANENT = "ecommerce/categories/{id}/permanent";
    
    // Cart APIs
    public static final String CART = "ecommerce/cart";
    public static final String CART_ADD = "ecommerce/cart/add";
    public static final String CART_UPDATE = "ecommerce/cart/update";
    public static final String CART_REMOVE = "ecommerce/cart/remove";
    public static final String CART_CLEAR = "ecommerce/cart/clear";
    public static final String CART_COUNT = "ecommerce/cart/count";
    public static final String CART_VALIDATE = "ecommerce/cart/validate";
    
    // Order APIs (you may need to implement these)
    public static final String ORDERS = "orders";
    public static final String ORDER_DETAIL = "orders/{id}";
    public static final String ORDER_STATUS = "orders/{id}/status";
    
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