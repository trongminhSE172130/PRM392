package com.fpt.project.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fpt.project.data.model.User;
import com.fpt.project.data.model.request.LoginRequest;
import com.fpt.project.data.model.request.RegisterRequest;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.response.LoginResponse;
import com.fpt.project.data.network.ApiService;
import com.fpt.project.data.network.NetworkClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    
    private static final String TAG = "AuthRepository";
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private Context context;
    
    public AuthRepository(Context context) {
        this.context = context;
        this.apiService = NetworkClient.getInstance(context).getApiService();
        this.sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }
    
    // Interface for callbacks
    public interface AuthCallback<T> {
        void onSuccess(T data);
        void onError(String message);
        void onFailure(String error);
    }
    
    // Login method
    public void login(String email, String password, AuthCallback<User> callback) {
        LoginRequest request = new LoginRequest(email, password);
        
        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.isSuccess()) {
                        User user = loginResponse.getUser();
                        String token = loginResponse.getToken();
                        
                        // Save user data, token and login state
                        saveUserData(user, token);
                        
                        callback.onSuccess(user);
                    } else {
                        callback.onError("Login failed: Invalid credentials");
                    }
                } else {
                    callback.onError("Login failed: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Register method
    public void register(String username, String email, String password, 
                        String fullName, String phone, String address, 
                        AuthCallback<User> callback) {
        
        RegisterRequest request = new RegisterRequest(username, email, password, fullName, phone, address);
        
        Call<ApiResponse<User>> call = apiService.register(request);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        User user = apiResponse.getData();
                        
                        // Save user data and login state
                        saveUserData(user);
                        
                        callback.onSuccess(user);
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Registration failed: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Register failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Logout method
    public void logout(AuthCallback<Object> callback) {
        Call<ApiResponse<Object>> call = apiService.logout();
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                // Clear local data regardless of server response
                clearUserData();
                callback.onSuccess(null);
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                // Clear local data even on failure
                clearUserData();
                callback.onSuccess(null);
            }
        });
    }
    
    // Get user profile
    public void getProfile(AuthCallback<User> callback) {
        Call<ApiResponse<User>> call = apiService.getProfile();
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get profile: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Get profile failure: " + t.getMessage());
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Helper methods for local storage
    private void saveUserData(User user, String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_email", user.getEmail());
        editor.putString("user_full_name", user.getFullName());
        editor.putString("user_username", user.getUsername());
        editor.putString("user_phone", user.getPhone());
        editor.putString("user_address", user.getAddress());
        editor.putString("user_id", user.getId());  // Now String instead of int
        editor.putString("user_role", user.getRole());
        
        // Save auth token
        editor.putString("auth_token", token);
        
        editor.apply();
    }
    
    // Overload for backward compatibility (without token)
    private void saveUserData(User user) {
        saveUserData(user, null);
    }
    
    private void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    
    // Check if user is logged in
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }
    
    // Get current user from local storage
    public User getCurrentUser() {
        if (!isUserLoggedIn()) {
            return null;
        }
        
        User user = new User();
        user.setId(sharedPreferences.getString("user_id", ""));  // Now String
        user.setEmail(sharedPreferences.getString("user_email", ""));
        user.setFullName(sharedPreferences.getString("user_full_name", ""));
        user.setUsername(sharedPreferences.getString("user_username", ""));
        user.setPhone(sharedPreferences.getString("user_phone", ""));
        user.setAddress(sharedPreferences.getString("user_address", ""));
        user.setRole(sharedPreferences.getString("user_role", ""));
        
        return user;
    }
    
    // Get current auth token
    public String getCurrentToken() {
        return sharedPreferences.getString("auth_token", null);
    }
} 