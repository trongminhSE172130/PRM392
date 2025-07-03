package com.fpt.project.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.fpt.project.MainActivity;
import com.fpt.project.R;
import com.fpt.project.data.model.User;
import com.fpt.project.data.repository.AuthRepository;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etUsername, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private MaterialButton btnSignUp;
    private View progressBar;
    private SharedPreferences sharedPreferences;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
        
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        authRepository = new AuthRepository(this);
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnSignUp.setOnClickListener(v -> performRegistration());
        
        findViewById(R.id.tvSignIn).setOnClickListener(v -> {
            finish(); // Go back to login activity
        });
    }

    private void performRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(fullName, username, email, phone, address, password, confirmPassword)) {
            return;
        }

        showLoading(true);
        
        // Call real API
        performRealRegistration(fullName, username, email, phone, address, password);
    }

    private boolean validateInput(String fullName, String username, String email, String phone, 
                                String address, String password, String confirmPassword) {
        
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performRealRegistration(String fullName, String username, String email, 
                                      String phone, String address, String password) {
        authRepository.register(username, email, password, fullName, phone, address, 
            new AuthRepository.AuthCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    });
                }
                
                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + message, Toast.LENGTH_LONG).show();
                    });
                }
                
                            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(RegisterActivity.this, 
                        "Cannot connect to server. Please check your internet connection.", 
                        Toast.LENGTH_LONG).show();
                });
            }
            });
    }



    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignUp.setEnabled(!show);
        btnSignUp.setText(show ? "" : "SIGN UP");
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
