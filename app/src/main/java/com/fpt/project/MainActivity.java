package com.fpt.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.fpt.project.ui.auth.LoginActivity;
import com.fpt.project.ui.cart.CartFragment;
import com.fpt.project.ui.category.CategoryFragment;
import com.fpt.project.ui.chat.ChatFragment;
import com.fpt.project.ui.home.HomeFragment;
import com.fpt.project.ui.map.MapFragment;
import com.fpt.project.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        
        // Check if user is logged in
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        setupBottomNavigation();
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
        
        // Handle navigation from payment result
        handleNavigationIntent();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_category) {
                fragment = new CategoryFragment();
            } else if (itemId == R.id.nav_map) {
                fragment = new MapFragment();
            } else if (itemId == R.id.nav_cart) {
                fragment = new CartFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }
            
            return fragment != null && loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    public void navigateToHome() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
    
    public void navigateToCart() {
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
    }
    
    public void navigateToChat() {
        // Since we don't have a chat tab, we'll create ChatFragment and navigate to it
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new com.fpt.project.ui.chat.ChatFragment())
                .addToBackStack("chat")
                .commit();
    }
    
    private void handleNavigationIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String navigateTo = intent.getStringExtra("navigate_to");
            if ("home".equals(navigateTo)) {
                navigateToHome();
            } else if ("cart".equals(navigateTo)) {
                navigateToCart();
            }
        }
    }
}
