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

    private TextInputEditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
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
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
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
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(fullName, email, phone, password, confirmPassword)) {
            return;
        }

        showLoading(true);
        
        // Call real API
        performRealRegistration(fullName, email, phone, password);
    }

    private boolean validateInput(String fullName, String email, String phone, 
                                String password, String confirmPassword) {
        
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (fullName.length() < 2) {
            etFullName.setError("Full name must be at least 2 characters");
            etFullName.requestFocus();
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

        // Vietnamese phone number validation
        if (!phone.matches("^(\\+84|0)([0-9]{9,10})$")) {
            etPhone.setError("Please enter a valid Vietnamese phone number (e.g., 0987654321)");
            etPhone.requestFocus();
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

    private void performRealRegistration(String fullName, String email, String phone, String password) {
        authRepository.register(email, password, fullName, phone, 
            new AuthRepository.AuthCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        if (user != null) {
                            String welcomeName = user.getFullName() != null ? user.getFullName() : "User";
                            Toast.makeText(RegisterActivity.this, 
                                "Welcome " + welcomeName + "! Registration successful!", 
                                Toast.LENGTH_SHORT).show();
                            navigateToMain();
                        } else {
                            Toast.makeText(RegisterActivity.this, 
                                "Registration successful but user data is invalid", 
                                Toast.LENGTH_LONG).show();
                        }
                    });
                }
                
                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(RegisterActivity.this, 
                            message != null ? message : "Registration failed", 
                            Toast.LENGTH_LONG).show();
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
