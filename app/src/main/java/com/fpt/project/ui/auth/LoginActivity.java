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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private View progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
        
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        
        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateToMain();
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        
        findViewById(R.id.tvSignUp).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        
        findViewById(R.id.tvForgotPassword).setOnClickListener(v -> {
            Toast.makeText(this, "Forgot password feature will be implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        showLoading(true);
        
        // TODO: Implement actual API call
        // For now, simulate login with dummy data
        simulateLogin(email, password);
    }

    private boolean validateInput(String email, String password) {
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

        return true;
    }

    private void simulateLogin(String email, String password) {
        // Simulate network delay
        btnLogin.postDelayed(() -> {
            showLoading(false);
            
            // For demo purposes, accept any valid email/password
            if (email.contains("@") && password.length() >= 6) {
                // Save login state
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", true);
                editor.putString("user_email", email);
                editor.apply();
                
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "" : "LOGIN");
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
