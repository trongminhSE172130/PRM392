package com.fpt.project.ui.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.Category;
import com.fpt.project.data.model.Product;
import com.fpt.project.data.model.Cart;
import com.fpt.project.data.model.request.AddToCartRequest;
import com.fpt.project.data.model.response.ProductResponse;
import com.fpt.project.data.repository.ProductRepository;
import com.fpt.project.data.repository.CartRepository;
import com.fpt.project.ui.home.ProductAdapter;
import com.fpt.project.ui.home.ProductDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductsActivity extends AppCompatActivity {

    private static final String TAG = "CategoryProducts";
    private static final String EXTRA_CATEGORY = "extra_category";
    
    // Views
    private Toolbar toolbar;
    private RecyclerView recyclerViewProducts;
    private View layoutLoading;
    private View layoutEmpty;
    private View layoutError;
    private TextView tvProductCount;
    private TextView tvEmptyMessage;
    private TextView tvErrorMessage;
    private MaterialButton btnBrowseAll;
    private MaterialButton btnRetry;
    
    // Data
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private Category category;

    public static Intent createIntent(Context context, Category category) {
        Intent intent = new Intent(context, CategoryProductsActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        getDataFromIntent();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        loadProducts();
    }

    @SuppressWarnings("deprecation")
    private void getDataFromIntent() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_CATEGORY)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                category = getIntent().getSerializableExtra(EXTRA_CATEGORY, Category.class);
            } else {
                category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY);
            }
        }
        
        if (category == null) {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        layoutError = findViewById(R.id.layoutError);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnBrowseAll = findViewById(R.id.btnBrowseAll);
        btnRetry = findViewById(R.id.btnRetry);
        
        productRepository = new ProductRepository(this);
        cartRepository = new CartRepository(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(category.getName());
        }
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this::onProductClick, this::onAddToCartClick);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        btnBrowseAll.setOnClickListener(v -> {
            // Navigate back to home or all products
            finish();
        });
        
        btnRetry.setOnClickListener(v -> loadProducts());
    }

    private void loadProducts() {
        Log.d(TAG, "Loading products for category: " + category.getName() + " (ID: " + category.getId() + ")");
        showLoading(true);
        
        productRepository.getProductsByCategory(1, 50, category.getId(), new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, ProductResponse.Pagination pagination) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    if (products != null && !products.isEmpty()) {
                        productList.clear();
                        productList.addAll(products);
                        productAdapter.notifyDataSetChanged();
                        showContent(true);
                        
                        // Update product count
                        String countText = products.size() == 1 ? 
                            products.size() + " product found" :
                            products.size() + " products found";
                        tvProductCount.setText(countText);
                        
                        Toast.makeText(CategoryProductsActivity.this, 
                            "Loaded " + products.size() + " products successfully!", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        showEmpty(true);
                        tvProductCount.setText("No products found");
                    }
                });
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error loading products: " + message);
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(true, message);
                    tvProductCount.setText("Failed to load products");
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Network failure: " + error);
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(true, "Network connection failed. Please check your internet connection.");
                    tvProductCount.setText("Connection failed");
                });
            }
        });
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewProducts.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        
        if (show) {
            tvProductCount.setText("Loading products...");
        }
    }

    private void showContent(boolean show) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewProducts.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showEmpty(boolean show) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewProducts.setVisibility(View.GONE);
        layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutError.setVisibility(View.GONE);
        
        if (show && tvEmptyMessage != null) {
            tvEmptyMessage.setText("No products found in " + category.getName() + " category");
        }
    }

    private void showError(boolean show, String errorMessage) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewProducts.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(show ? View.VISIBLE : View.GONE);
        
        if (show && tvErrorMessage != null) {
            tvErrorMessage.setText(errorMessage != null ? errorMessage : "Unknown error occurred");
        }
    }

    private void onProductClick(Product product) {
        Log.d(TAG, "Product clicked: " + product.getName());
        
        if (product != null) {
            // Navigate to ProductDetailActivity with the selected product
            Intent intent = ProductDetailActivity.createIntent(this, product);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Product information not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void onAddToCartClick(Product product) {
        Log.d(TAG, "Add to cart clicked: " + product.getName());
        
        if (product == null) {
            Toast.makeText(this, "Product not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if product is available
        if (!product.isActive() || product.getStockQuantity() <= 0) {
            Toast.makeText(this, 
                product.isActive() ? "Product is out of stock" : "Product is not available", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading message
        Toast.makeText(this, "Adding " + product.getName() + " to cart...", Toast.LENGTH_SHORT).show();
        
        // Create request to add 1 item to cart
        AddToCartRequest request = new AddToCartRequest(product.getId(), 1);
        
        cartRepository.addToCart(request, new CartRepository.CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                runOnUiThread(() -> {
                    if (cart != null) {
                        Toast.makeText(CategoryProductsActivity.this, 
                            product.getName() + " added to cart!\nTotal: " + cart.getTotalItems() + " items", 
                            Toast.LENGTH_LONG).show();
                        
                        Log.d(TAG, "Cart updated - Total items: " + cart.getTotalItems() + 
                              ", Total amount: $" + cart.getTotalAmount());
                    } else {
                        Toast.makeText(CategoryProductsActivity.this, 
                            product.getName() + " added to cart successfully!", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onError(String message) {
                Log.e(TAG, "Add to cart error: " + message);
                runOnUiThread(() -> {
                    Toast.makeText(CategoryProductsActivity.this, 
                        "Failed to add to cart: " + message, 
                        Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Add to cart failure: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(CategoryProductsActivity.this, 
                        "Network error. Please check your connection.", 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                getOnBackPressedDispatcher().onBackPressed();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 