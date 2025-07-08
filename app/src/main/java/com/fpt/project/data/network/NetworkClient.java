package com.fpt.project.data.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fpt.project.data.model.ConversationIdDeserializer;
import com.fpt.project.data.model.ConversationInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    
    private static final String TAG = "NetworkClient";
    private static NetworkClient instance;
    private Retrofit retrofit;
    private Context context;
    
    private NetworkClient(Context context) {
        this.context = context.getApplicationContext();
        setupRetrofit();
    }
    
    public static synchronized NetworkClient getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkClient(context);
        }
        return instance;
    }
    
    private void setupRetrofit() {
        // Create Gson with custom configuration
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(ConversationInfo.class, new ConversationIdDeserializer())
                .create();
        
        // Create OkHttpClient with interceptors
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS);
        
        // Add logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);
        
        // Add auth interceptor to automatically add token to requests
        httpClient.addInterceptor(new AuthInterceptor());
        
        // Build Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
    }
    
    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
    
    // Auth Interceptor to add Authorization header or x-session-id
    private class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String url = originalRequest.url().toString();
            
            // Get authentication data from SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String token = prefs.getString("auth_token", null);
            boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
            
            Log.d(TAG, "Intercepting request to: " + url);
            Log.d(TAG, "User logged in: " + isLoggedIn);
            Log.d(TAG, "Token found: " + (token != null ? "YES (length=" + token.length() + ")" : "NO"));
            
            Request.Builder requestBuilder = originalRequest.newBuilder();
            
            // If user is logged in and has token, use Bearer token
            if (isLoggedIn && token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
                Log.d(TAG, "Added Authorization header to request");
            } else {
                // If user is not logged in, use session ID for anonymous access
                String sessionId = getOrCreateSessionId(prefs);
                requestBuilder.header("x-session-id", sessionId);
                Log.d(TAG, "Added x-session-id header: " + sessionId);
            }
            
            return chain.proceed(requestBuilder.build());
        }
        
        private String getOrCreateSessionId(SharedPreferences prefs) {
            String sessionId = prefs.getString("session_id", null);
            if (sessionId == null || sessionId.isEmpty()) {
                // Generate new session ID
                sessionId = "sess_" + System.currentTimeMillis() + "_" + Math.random();
                prefs.edit().putString("session_id", sessionId).apply();
                Log.d(TAG, "Generated new session ID: " + sessionId);
            }
            return sessionId;
        }
    }
    
    // Method to update base URL if needed
    public void updateBaseUrl(String newBaseUrl) {
        // Recreate retrofit with new base URL
        setupRetrofit();
    }
} 