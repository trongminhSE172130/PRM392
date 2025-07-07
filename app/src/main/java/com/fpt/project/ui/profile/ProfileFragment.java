package com.fpt.project.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fpt.project.R;
import com.fpt.project.data.model.User;
import com.fpt.project.data.repository.AuthRepository;
import com.fpt.project.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView tvUserName, tvUserEmail, tvUserRole;
    private MaterialButton btnLogout, btnDebugToken;
    private ImageView ivRefresh;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ProfileFragment onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        setupClickListeners();
        loadUserProfile();
        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserRole = view.findViewById(R.id.tvUserRole);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDebugToken = view.findViewById(R.id.btnDebugToken);
        ivRefresh = view.findViewById(R.id.ivRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        
        if (getContext() != null) {
            sharedPreferences = getContext().getSharedPreferences("app_prefs", MODE_PRIVATE);
            authRepository = new AuthRepository(getContext());
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> performLogout());
        btnDebugToken.setOnClickListener(v -> performDebugToken());
        ivRefresh.setOnClickListener(v -> refreshProfile());
    }
    
    private void refreshProfile() {
        Toast.makeText(getContext(), "Refreshing profile...", Toast.LENGTH_SHORT).show();
        loadProfileFromAPI();
    }
    
    private void performDebugToken() {
        StringBuilder debugInfo = new StringBuilder();
        
        if (sharedPreferences != null) {
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
            String email = sharedPreferences.getString("user_email", "");
            String fullName = sharedPreferences.getString("user_full_name", "");
            String role = sharedPreferences.getString("user_role", "");
            
            debugInfo.append("Login Status: ").append(isLoggedIn).append("\n");
            debugInfo.append("Email: ").append(email).append("\n");
            debugInfo.append("Name: ").append(fullName).append("\n");
            debugInfo.append("Role: ").append(role).append("\n");
        }
        
        if (authRepository != null) {
            String token = authRepository.getCurrentToken();
            if (token != null) {
                debugInfo.append("Token: EXISTS (").append(token.length()).append(" chars)\n");
                debugInfo.append("Token preview: ").append(token.substring(0, Math.min(20, token.length()))).append("...\n");
            } else {
                debugInfo.append("Token: NULL\n");
            }
        }
        
        Log.i(TAG, "=== TOKEN DEBUG INFO ===\n" + debugInfo.toString());
        
        // Show in toast (truncated)
        String shortInfo = "Login: " + sharedPreferences.getBoolean("is_logged_in", false) + 
                          ", Token: " + (authRepository.getCurrentToken() != null ? "EXISTS" : "NULL");
        Toast.makeText(getContext(), shortInfo, Toast.LENGTH_LONG).show();
    }

    private void loadUserProfile() {
        Log.d(TAG, "Loading user profile...");
        
        // Show local data first (faster UX)
        showLocalData();
        
        // Validate login status and load API data
        if (validateLoginStatus()) {
            Log.d(TAG, "Login status valid, loading from API");
            loadProfileFromAPI();
        } else {
            Log.w(TAG, "Login status invalid, showing local data only");
            Toast.makeText(getContext(), "Please login again to sync latest data", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateLoginStatus() {
        if (sharedPreferences == null) {
            Log.w(TAG, "SharedPreferences is null");
            return false;
        }
        
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String email = sharedPreferences.getString("user_email", "");
        String token = authRepository != null ? authRepository.getCurrentToken() : null;
        
        Log.d(TAG, "Validating login - isLoggedIn: " + isLoggedIn + 
                   ", hasEmail: " + !email.isEmpty() +
                   ", hasToken: " + (token != null && !token.isEmpty()));
        
        // Basic validation: just need to be logged in with email
        // Token can be missing temporarily but we still show profile
        boolean isValid = isLoggedIn && !email.isEmpty();
        
        if (!isValid) {
            Log.w(TAG, "Basic login validation failed");
        }
        
        return isValid;
    }

    private void showLocalData() {
        if (sharedPreferences != null) {
            String fullName = sharedPreferences.getString("user_full_name", "User Name");
            String email = sharedPreferences.getString("user_email", "user@example.com");
            String role = sharedPreferences.getString("user_role", "User");
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
            
            Log.d(TAG, "Showing local data - Name: " + fullName + ", Email: " + email + ", Role: " + role);
            
            // Show data if available, otherwise show defaults
            tvUserName.setText(fullName);
            tvUserEmail.setText(email);
            tvUserRole.setText(role.toUpperCase());
            
            // If not actually logged in, show warning
            if (!isLoggedIn || email.equals("user@example.com")) {
                Log.w(TAG, "No valid local data, user should login");
            }
        } else {
            Log.e(TAG, "SharedPreferences is null, showing default values");
            tvUserName.setText("User Name");
            tvUserEmail.setText("user@example.com");
            tvUserRole.setText("USER");
        }
    }

    private void loadProfileFromAPI() {
        if (authRepository == null) {
            Log.w(TAG, "AuthRepository is null, cannot load profile");
            return;
        }
        
        // Debug current token
        debugCurrentToken();
        
        Log.d(TAG, "Loading profile from API...");
        showLoading(true);
        
        authRepository.getProfile(new AuthRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Profile API success, user: " + (user != null ? user.getEmail() : "null"));
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (user != null) {
                            updateUserInterface(user);
                            saveUpdatedUserData(user);
                            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "User object is null from API");
                            Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                Log.e(TAG, "Profile API error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        
                        // Handle 401 - token invalid, but don't redirect immediately
                        if (message != null && message.contains("401")) {
                            Log.w(TAG, "Token invalid (401), user needs to re-login");
                            Toast.makeText(getContext(), 
                                "Session expired. Profile data may be outdated. Please logout and login again.", 
                                Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), 
                                "Failed to refresh profile: " + message, 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Profile API failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(getContext(), 
                            "Cannot connect to server: " + error, 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void debugCurrentToken() {
        Log.d(TAG, "=== DEBUG CURRENT TOKEN START ===");
        
        if (authRepository != null) {
            Log.d(TAG, "AuthRepository exists, calling getCurrentToken()...");
            String token = authRepository.getCurrentToken();
            Log.d(TAG, "getCurrentToken() returned: " + (token != null ? "EXISTS (length=" + token.length() + ")" : "NULL"));
            
            if (sharedPreferences != null) {
                boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
                String savedEmail = sharedPreferences.getString("user_email", "");
                String directToken = sharedPreferences.getString("auth_token", null);
                
                Log.d(TAG, "Direct SharedPreferences check:");
                Log.d(TAG, "- is_logged_in: " + isLoggedIn);
                Log.d(TAG, "- user_email: " + (savedEmail.isEmpty() ? "EMPTY" : savedEmail));
                Log.d(TAG, "- auth_token (direct): " + (directToken != null ? "EXISTS (length=" + directToken.length() + ")" : "NULL"));
                
                // Compare values
                if (token != null && directToken != null) {
                    Log.d(TAG, "Token comparison: " + (token.equals(directToken) ? "MATCH" : "MISMATCH"));
                } else if (token == null && directToken == null) {
                    Log.w(TAG, "Both tokens are NULL - this is the problem!");
                } else {
                    Log.e(TAG, "Token mismatch - one is null, one is not!");
                }
            } else {
                Log.e(TAG, "SharedPreferences is NULL!");
            }
        } else {
            Log.e(TAG, "AuthRepository is NULL!");
        }
        
        Log.d(TAG, "=== DEBUG CURRENT TOKEN END ===");
    }

    private void updateUserInterface(User user) {
        String fullName = user.getFullName() != null ? user.getFullName() : "User Name";
        String email = user.getEmail() != null ? user.getEmail() : "user@example.com";
        String role = user.getRole() != null ? user.getRole() : "user";
        
        tvUserName.setText(fullName);
        tvUserEmail.setText(email);
        tvUserRole.setText(role.toUpperCase());
    }

    private void saveUpdatedUserData(User user) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            
            if (user.getFullName() != null) {
                editor.putString("user_full_name", user.getFullName());
            }
            if (user.getEmail() != null) {
                editor.putString("user_email", user.getEmail());
            }
            if (user.getRole() != null) {
                editor.putString("user_role", user.getRole());
            }
            if (user.getId() != null) {
                editor.putString("user_id", user.getId());
            }
            
            editor.apply();
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void performLogout() {
        if (authRepository != null) {
            authRepository.logout(new AuthRepository.AuthCallback<Object>() {
                @Override
                public void onSuccess(Object data) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        });
                    }
                }
                
                @Override
                public void onError(String message) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            // Still navigate to login even if logout API fails
                            navigateToLogin();
                        });
                    }
                }
                
                @Override
                public void onFailure(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            // Still navigate to login even if network fails
                            navigateToLogin();
                        });
                    }
                }
            });
        } else {
            // Fallback: clear local data and navigate
            clearLocalData();
            navigateToLogin();
        }
    }

    private void clearLocalData() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}

