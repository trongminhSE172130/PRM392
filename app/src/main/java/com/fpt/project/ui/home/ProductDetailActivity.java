package com.fpt.project.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fpt.project.R;
import com.fpt.project.data.model.Cart;
import com.fpt.project.data.model.Product;
import com.fpt.project.data.model.request.AddToCartRequest;
import com.fpt.project.data.repository.CartRepository;
import com.fpt.project.data.repository.ProductRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT = "extra_product";

    // Views
    private MaterialToolbar toolbar;
    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductCategory;
    private TextView tvProductSku;
    private TextView tvStockStatus;
    private TextView tvProductDescription;
    private TextView tvQuantity;
    private MaterialButton btnDecrease;
    private MaterialButton btnIncrease;
    private MaterialButton btnAddToCart;
    private View stockIndicator;

    // Data
    private Product product;
    private int quantity = 1;
    private ProductRepository productRepository;
    private CartRepository cartRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initRepositories();
        initViews();
        setupToolbar();
        loadProductData();
        setupClickListeners();
    }

    private void initRepositories() {
        productRepository = new ProductRepository(this);
        cartRepository = new CartRepository(this);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductCategory = findViewById(R.id.tvProductCategory);
        tvProductSku = findViewById(R.id.tvProductSku);
        tvStockStatus = findViewById(R.id.tvStockStatus);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        stockIndicator = findViewById(R.id.stockIndicator);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @SuppressWarnings("deprecation")
    private void loadProductData() {
        // Try to get product from Intent extras first (passed directly)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            product = getIntent().getSerializableExtra(EXTRA_PRODUCT, Product.class);
        } else {
            product = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        }
        
        if (product != null) {
            displayProductInfo(product);
        } else {
            // If no product passed directly, try to get by ID
            String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
            if (productId != null) {
                loadProductById(productId);
            } else {
                Log.e(TAG, "No product data provided");
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void loadProductById(String productId) {
        Log.d(TAG, "Loading product by ID: " + productId);
        
        // Note: API endpoint expects int but our model uses String
        // For now, we'll show error. You may need to update API or handle conversion
        
        try {
            int id = Integer.parseInt(productId.replaceAll("[^0-9]", ""));
            productRepository.getProductDetail(id, new ProductRepository.ProductCallback<Product>() {
                @Override
                public void onSuccess(Product loadedProduct) {
                    runOnUiThread(() -> {
                        product = loadedProduct;
                        displayProductInfo(product);
                    });
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "Error loading product: " + message);
                    runOnUiThread(() -> {
                        Toast.makeText(ProductDetailActivity.this, 
                            "Failed to load product: " + message, 
                            Toast.LENGTH_LONG).show();
                        finish();
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Network error: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(ProductDetailActivity.this, 
                            "Network error. Please check your connection.", 
                            Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid product ID format: " + productId);
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayProductInfo(Product product) {
        Log.d(TAG, "Displaying product: " + product.getName());

        // Product name and set toolbar title
        tvProductName.setText(product.getName());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(product.getName());
        }

        // Price
        tvProductPrice.setText(String.format("$%.2f", product.getPrice()));

        // Category
        String categoryName = product.getCategoryName();
        if (categoryName != null && !categoryName.isEmpty()) {
            tvProductCategory.setText("Category: " + categoryName);
            tvProductCategory.setVisibility(View.VISIBLE);
        } else {
            tvProductCategory.setVisibility(View.GONE);
        }

        // SKU
        if (product.getSku() != null && !product.getSku().isEmpty()) {
            tvProductSku.setText("SKU: " + product.getSku());
            tvProductSku.setVisibility(View.VISIBLE);
        } else {
            tvProductSku.setVisibility(View.GONE);
        }

        // Stock status
        updateStockStatus(product);

        // Description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvProductDescription.setText(product.getDescription());
        } else {
            tvProductDescription.setText("No description available.");
        }

        // Product image
        loadProductImage(product);

        // Update add to cart button based on stock
        updateAddToCartButton();
    }

    private void updateStockStatus(Product product) {
        int stock = product.getStockQuantity();
        boolean isActive = product.isActive();

        if (!isActive) {
            tvStockStatus.setText("Product not available");
            stockIndicator.setBackgroundTintList(getColorStateList(android.R.color.holo_red_light));
            btnAddToCart.setEnabled(false);
        } else if (stock <= 0) {
            tvStockStatus.setText("Out of Stock");
            stockIndicator.setBackgroundTintList(getColorStateList(android.R.color.holo_red_light));
            btnAddToCart.setEnabled(false);
        } else if (stock <= 10) {
            tvStockStatus.setText("Low Stock (" + stock + " available)");
            stockIndicator.setBackgroundTintList(getColorStateList(android.R.color.holo_orange_light));
            btnAddToCart.setEnabled(true);
        } else {
            tvStockStatus.setText("In Stock (" + stock + " available)");
            stockIndicator.setBackgroundTintList(getColorStateList(android.R.color.holo_green_light));
            btnAddToCart.setEnabled(true);
        }
    }

    private void loadProductImage(Product product) {
        String imageUrl = product.getPrimaryImageUrl();
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // TODO: Use Glide to load image from URL
            // For now, use placeholder
            Log.d(TAG, "Product image URL: " + imageUrl);
            ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void setupClickListeners() {
        // Quantity controls
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityDisplay();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (product != null && quantity < product.getStockQuantity()) {
                quantity++;
                updateQuantityDisplay();
            } else {
                Toast.makeText(this, "Cannot exceed available stock", Toast.LENGTH_SHORT).show();
            }
        });

        // Add to cart
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void updateQuantityDisplay() {
        tvQuantity.setText(String.valueOf(quantity));
        updateAddToCartButton();
    }

    private void updateAddToCartButton() {
        if (product == null) return;

        boolean canAddToCart = product.isActive() && 
                              product.getStockQuantity() > 0 && 
                              quantity <= product.getStockQuantity();

        btnAddToCart.setEnabled(canAddToCart);
        
        if (canAddToCart) {
            double totalPrice = product.getPrice() * quantity;
            btnAddToCart.setText(String.format("Add to Cart - $%.2f", totalPrice));
        } else {
            btnAddToCart.setText("Add to Cart");
        }
    }

    private void addToCart() {
        if (product == null) {
            Toast.makeText(this, "Product information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Adding to cart: " + product.getName() + " x " + quantity);

        // Create AddToCartRequest
        AddToCartRequest request = new AddToCartRequest(
            product.getId(),
            quantity
        );

        // Show loading state
        btnAddToCart.setEnabled(false);
        btnAddToCart.setText("Adding to Cart...");

        // Add to cart via repository
        cartRepository.addToCart(request, new CartRepository.CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                runOnUiThread(() -> {
                    btnAddToCart.setEnabled(true);
                    updateAddToCartButton();
                    
                    if (cart != null) {
                        Toast.makeText(ProductDetailActivity.this, 
                            product.getName() + " added to cart!\n" +
                            "Cart: " + cart.getTotalItems() + " items ($" + String.format("%.2f", cart.getTotalAmount()) + ")", 
                            Toast.LENGTH_LONG).show();
                        
                        Log.d(TAG, "Cart updated successfully - Total items: " + cart.getTotalItems() + 
                              ", Total amount: $" + cart.getTotalAmount());
                    } else {
                        Toast.makeText(ProductDetailActivity.this, 
                            quantity + "x " + product.getName() + " added to cart!", 
                            Toast.LENGTH_SHORT).show();
                    }
                    
                    // Reset quantity to 1
                    resetQuantity();
                });
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error adding to cart: " + message);
                runOnUiThread(() -> {
                    btnAddToCart.setEnabled(true);
                    updateAddToCartButton();
                    
                    Toast.makeText(ProductDetailActivity.this, 
                        "Failed to add to cart: " + message, 
                        Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Network error adding to cart: " + error);
                runOnUiThread(() -> {
                    btnAddToCart.setEnabled(true);
                    updateAddToCartButton();
                    
                    Toast.makeText(ProductDetailActivity.this, 
                        "Network error. Please try again.", 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void resetQuantity() {
        quantity = 1;
        updateQuantityDisplay();
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

    // Static method to create intent for this activity
    public static Intent createIntent(android.content.Context context, Product product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        return intent;
    }

    public static Intent createIntent(android.content.Context context, String productId) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        return intent;
    }
} 