package com.fpt.project.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fpt.project.data.model.User;
import com.fpt.project.data.model.request.LoginRequest;
import com.fpt.project.data.model.request.RegisterRequest;
import com.fpt.project.data.model.response.ApiResponse;
import com.fpt.project.data.model.response.LoginResponse;
import com.fpt.project.data.model.response.RegisterResponse;
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
        Log.d(TAG, "Starting login for email: " + email);
        LoginRequest request = new LoginRequest(email, password);
        
        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Login response received. Code: " + response.code());
                Log.d(TAG, "Response body is null: " + (response.body() == null));
                
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Login response success: " + loginResponse.isSuccess());
                    Log.d(TAG, "Login response message: " + loginResponse.getMessage());
                    
                    if (loginResponse.isSuccess()) {
                        User user = loginResponse.getUser();
                        String token = loginResponse.getToken();
                        
                        Log.d(TAG, "=== LOGIN API RESPONSE DEBUG ===");
                        Log.d(TAG, "User object: " + (user != null ? "EXISTS" : "NULL"));
                        if (user != null) {
                            Log.d(TAG, "User ID: " + user.getId());
                            Log.d(TAG, "User Email: " + user.getEmail());
                            Log.d(TAG, "User FullName: " + user.getFullName());
                            Log.d(TAG, "User Role: " + user.getRole());
                            Log.d(TAG, "User Phone: " + user.getPhone());
                        }
                        Log.d(TAG, "Token: " + (token != null ? "YES (length=" + token.length() + ")" : "NULL"));
                        Log.d(TAG, "================================");
                        
                        if (user != null) {
                            // Save user data, token and login state
                            Log.d(TAG, "Calling saveUserData...");
                            saveUserData(user, token);
                            Log.d(TAG, "saveUserData completed, calling success callback");
                            callback.onSuccess(user);
                        } else {
                            Log.e(TAG, "User object is null in response");
                            callback.onError("Login failed: Invalid user data received");
                        }
                    } else {
                        String errorMessage = loginResponse.getMessage() != null ? 
                            loginResponse.getMessage() : "Invalid credentials";
                        Log.w(TAG, "Login failed: " + errorMessage);
                        callback.onError("Login failed: " + errorMessage);
                    }
                } else {
                    Log.e(TAG, "Login failed with code: " + response.code());
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    callback.onError("Login failed: " + response.code() + " " + errorBody);
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login network failure: " + t.getMessage(), t);
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    
    // Register method  
    public void register(String email, String password, String fullName, String phone,
                        AuthCallback<User> callback) {
        
        Log.d(TAG, "Starting registration for email: " + email);
        RegisterRequest request = new RegisterRequest(email, password, fullName, phone);
        
        Call<RegisterResponse> call = apiService.register(request);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d(TAG, "Register response received. Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    Log.d(TAG, "Register response success: " + registerResponse.isSuccess());
                    
                    if (registerResponse.isSuccess()) {
                        User user = registerResponse.getUser();
                        String token = registerResponse.getToken();
                        
                        Log.d(TAG, "User received: " + (user != null ? user.getEmail() : "null"));
                        Log.d(TAG, "Token received: " + (token != null ? "yes" : "null"));
                        
                        if (user != null) {
                            // Save user data, token and login state
                            saveUserData(user, token);
                            callback.onSuccess(user);
                        } else {
                            Log.e(TAG, "User object is null in response");
                            callback.onError("Registration failed: Invalid user data received");
                        }
                    } else {
                        String errorMessage = registerResponse.getMessage() != null ? 
                            registerResponse.getMessage() : "Registration failed";
                        Log.w(TAG, "Registration failed: " + errorMessage);
                        callback.onError("Registration failed: " + errorMessage);
                    }
                } else {
                    Log.e(TAG, "Registration failed with code: " + response.code());
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    callback.onError("Registration failed: " + response.code() + " " + errorBody);
                }
            }
            
            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e(TAG, "Register network failure: " + t.getMessage(), t);
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
        if (user == null) {
            Log.w(TAG, "User object is null, cannot save user data");
            return;
        }
        
        Log.d(TAG, "=== SAVE USER DATA DEBUG ===");
        Log.d(TAG, "User Email: " + (user.getEmail() != null ? user.getEmail() : "NULL"));
        Log.d(TAG, "User Name: " + (user.getFullName() != null ? user.getFullName() : "NULL"));
        Log.d(TAG, "User Role: " + (user.getRole() != null ? user.getRole() : "NULL"));
        Log.d(TAG, "Token received: " + (token != null ? "YES (length=" + token.length() + ")" : "NULL"));
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        
        // Use null-safe methods to avoid crashes
        editor.putString("user_email", user.getEmail() != null ? user.getEmail() : "");
        editor.putString("user_full_name", user.getFullName() != null ? user.getFullName() : "");
        editor.putString("user_username", user.getUsername() != null ? user.getUsername() : "");
        editor.putString("user_phone", user.getPhone() != null ? user.getPhone() : "");
        editor.putString("user_address", user.getAddress() != null ? user.getAddress() : "");
        editor.putString("user_id", user.getId() != null ? user.getId() : "");
        editor.putString("user_role", user.getRole() != null ? user.getRole() : "");
        
        // Save auth token
        if (token != null) {
            editor.putString("auth_token", token);
            Log.d(TAG, "Token saved to SharedPreferences: YES");
        } else {
            Log.w(TAG, "Token is NULL - not saving to SharedPreferences");
        }
        
        boolean success = editor.commit(); // Use commit instead of apply for immediate feedback
        Log.d(TAG, "SharedPreferences commit result: " + success);
        
        // Verify data was saved correctly
        String savedToken = sharedPreferences.getString("auth_token", null);
        String savedEmail = sharedPreferences.getString("user_email", null);
        boolean savedLoginStatus = sharedPreferences.getBoolean("is_logged_in", false);
        
        Log.d(TAG, "=== VERIFICATION AFTER SAVE ===");
        Log.d(TAG, "Saved login status: " + savedLoginStatus);
        Log.d(TAG, "Saved email: " + (savedEmail != null ? savedEmail : "NULL"));
        Log.d(TAG, "Saved token: " + (savedToken != null ? "YES (length=" + savedToken.length() + ")" : "NULL"));
        Log.d(TAG, "==============================");
        
        Log.d(TAG, "User data saved successfully for: " + user.getEmail());
    }
    
    // Overload for backward compatibility (without token)
    private void saveUserData(User user) {
        saveUserData(user, null);
    }
    
    private void clearUserData() {
        // Preserve session_id for anonymous access after logout
        String sessionId = sharedPreferences.getString("session_id", null);
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        
        // Restore session_id if it existed
        if (sessionId != null) {
            editor.putString("session_id", sessionId);
            Log.d(TAG, "Preserved session_id after logout: " + sessionId);
        }
        
        editor.apply();
        Log.d(TAG, "User data cleared, logout completed");
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
        String token = sharedPreferences.getString("auth_token", null);
        Log.d(TAG, "=== GET TOKEN DEBUG ===");
        Log.d(TAG, "Token retrieved: " + (token != null ? "YES (length=" + token.length() + ")" : "NULL"));
        Log.d(TAG, "=======================");
        return token;
    }
} 