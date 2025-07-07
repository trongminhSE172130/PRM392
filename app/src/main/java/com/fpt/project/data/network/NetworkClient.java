package com.fpt.project.data.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    
    // Auth Interceptor to add Authorization header
    private class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String url = originalRequest.url().toString();
            
            // Get token from SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String token = prefs.getString("auth_token", null);
            
            Log.d(TAG, "Intercepting request to: " + url);
            Log.d(TAG, "Token found: " + (token != null ? "YES (length=" + token.length() + ")" : "NO"));
            
            // Add authorization header if token exists
            if (token != null && !token.isEmpty()) {
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
                Log.d(TAG, "Added Authorization header to request");
                return chain.proceed(newRequest);
            } else {
                Log.w(TAG, "No token available, proceeding without Authorization header");
            }
            
            return chain.proceed(originalRequest);
        }
    }
    
    // Method to update base URL if needed
    public void updateBaseUrl(String newBaseUrl) {
        // Recreate retrofit with new base URL
        setupRetrofit();
    }
} 