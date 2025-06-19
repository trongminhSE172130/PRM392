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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etUsername, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private MaterialButton btnSignUp;
    private View progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
        
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
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
        
        // TODO: Implement actual API call
        // For now, simulate registration
        simulateRegistration(fullName, username, email, phone, address, password);
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

    private void simulateRegistration(String fullName, String username, String email, 
                                    String phone, String address, String password) {
        // Simulate network delay
        btnSignUp.postDelayed(() -> {
            showLoading(false);
            
            // For demo purposes, always succeed
            // Save user data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putString("user_email", email);
            editor.putString("user_full_name", fullName);
            editor.putString("user_username", username);
            editor.putString("user_phone", phone);
            editor.putString("user_address", address);
            editor.apply();
            
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        }, 2000);
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
