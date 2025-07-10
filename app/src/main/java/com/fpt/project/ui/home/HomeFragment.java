package com.fpt.project.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.fpt.project.R;
import com.fpt.project.data.model.Cart;
import com.fpt.project.data.model.Category;
import com.fpt.project.data.model.Product;
import com.fpt.project.data.model.request.AddToCartRequest;
import com.fpt.project.data.model.response.CategoryResponse;
import com.fpt.project.data.model.response.ProductResponse;
import com.fpt.project.data.repository.CartRepository;
import com.fpt.project.data.repository.CategoryRepository;
import com.fpt.project.data.repository.ProductRepository;
import com.fpt.project.ui.category.CategoryAdapter;
import com.fpt.project.ui.category.CategoryProductsActivity;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvWelcome, tvUserName;
    private ImageView ivNotification, ivChat, ivFilter;
    private EditText etSearch;
    private ViewPager2 viewPagerBanner;
    
    // Categories Section
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private TextView tvSeeAllCategories;
    
    // Featured Products Section
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> featuredProductList;
    private TextView tvSeeAllFeatured;
    
    // Popular Products Section
    private RecyclerView recyclerViewPopularProducts;
    private ProductAdapter popularProductAdapter;
    private List<Product> popularProductList;
    private TextView tvSeeAllPopular;

    // Data
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private CategoryRepository categoryRepository;
    private SharedPreferences sharedPreferences;
    
    // Search functionality
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        initRepositories();
        setupRecyclerViews();
        setupClickListeners();
        setupSearchFunctionality();
        setupSwipeRefresh();
        updateWelcomeMessage();
        loadData();
        return view;
    }

    private void initViews(View view) {
        // Main views
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvUserName = view.findViewById(R.id.tvUserName);
        ivNotification = view.findViewById(R.id.ivNotification);
        ivChat = view.findViewById(R.id.ivChat);
        etSearch = view.findViewById(R.id.etSearch);
        ivFilter = view.findViewById(R.id.ivFilter);
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        
        // Categories section
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        tvSeeAllCategories = view.findViewById(R.id.tvSeeAllCategories);
        
        // Featured products section
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        tvSeeAllFeatured = view.findViewById(R.id.tvSeeAllFeatured);
        
        // Popular products section
        recyclerViewPopularProducts = view.findViewById(R.id.recyclerViewPopularProducts);
        tvSeeAllPopular = view.findViewById(R.id.tvSeeAllPopular);
    }

    private void initRepositories() {
        productRepository = new ProductRepository(getContext());
        cartRepository = new CartRepository(getContext());
        categoryRepository = new CategoryRepository(getContext());
        
        if (getContext() != null) {
            sharedPreferences = getContext().getSharedPreferences("app_prefs", MODE_PRIVATE);
        }
    }

    private void setupRecyclerViews() {
        // Categories RecyclerView (horizontal)
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, this::onCategoryClick);
        LinearLayoutManager categoriesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategories.setLayoutManager(categoriesLayoutManager);
        recyclerViewCategories.setAdapter(categoryAdapter);
        
        // Featured Products RecyclerView (grid)
        featuredProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(featuredProductList, this::onProductClick, this::onAddToCartClick);
        GridLayoutManager featuredLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(featuredLayoutManager);
        recyclerViewProducts.setAdapter(productAdapter);
        
        // Popular Products RecyclerView (grid)
        popularProductList = new ArrayList<>();
        popularProductAdapter = new ProductAdapter(popularProductList, this::onProductClick, this::onAddToCartClick);
        GridLayoutManager popularLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewPopularProducts.setLayoutManager(popularLayoutManager);
        recyclerViewPopularProducts.setAdapter(popularProductAdapter);
    }

    private void setupClickListeners() {
        // Header icons
        ivNotification.setOnClickListener(v -> {
            // Navigate to settings or notifications
            Toast.makeText(getContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        ivChat.setOnClickListener(v -> {
            // Navigate to chat fragment via bottom navigation
            if (getActivity() != null && getActivity() instanceof com.fpt.project.MainActivity) {
                com.fpt.project.MainActivity mainActivity = (com.fpt.project.MainActivity) getActivity();
                mainActivity.navigateToChat();
            }
        });

        ivFilter.setOnClickListener(v -> {
            // Open filter dialog
            Toast.makeText(getContext(), "Filter clicked", Toast.LENGTH_SHORT).show();
        });

        // See all buttons
        tvSeeAllCategories.setOnClickListener(v -> {
            // Navigate to categories tab
            if (getActivity() != null && getActivity() instanceof com.fpt.project.MainActivity) {
                com.fpt.project.MainActivity mainActivity = (com.fpt.project.MainActivity) getActivity();
                // Assuming there's a navigation method for categories
                // mainActivity.navigateToCategories();
            }
        });

        tvSeeAllFeatured.setOnClickListener(v -> {
            // Navigate to all featured products
            Toast.makeText(getContext(), "See all featured products", Toast.LENGTH_SHORT).show();
        });

        tvSeeAllPopular.setOnClickListener(v -> {
            // Navigate to all popular products
            Toast.makeText(getContext(), "See all popular products", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSearchFunctionality() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                // Create new search runnable with delay
                searchRunnable = () -> performSearch(s.toString().trim());
                searchHandler.postDelayed(searchRunnable, 500); // 500ms delay
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            R.color.design_default_color_primary,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
        
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    private void updateWelcomeMessage() {
        // Update welcome message based on time of day
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        String greeting;
        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 17) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }
        
        tvWelcome.setText(greeting);
        
        // Get user name from preferences
        if (sharedPreferences != null) {
            String userName = sharedPreferences.getString("user_full_name", "Welcome Back!");
            if (userName != null && !userName.isEmpty() && !userName.equals("Welcome Back!")) {
                tvUserName.setText(userName);
            } else {
                tvUserName.setText("Welcome Back!");
            }
        }
    }

    private void loadData() {
        loadCategories();
        loadFeaturedProducts();
        loadPopularProducts();
        // loadBanners(); // Implement if you have banner data
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);
        
        // Reset all data
        categoryList.clear();
        featuredProductList.clear();
        popularProductList.clear();
        
        // Notify adapters
        if (categoryAdapter != null) categoryAdapter.notifyDataSetChanged();
        if (productAdapter != null) productAdapter.notifyDataSetChanged();
        if (popularProductAdapter != null) popularProductAdapter.notifyDataSetChanged();
        
        // Reload data
        loadData();
        
        // Stop refresh animation after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private void loadCategories() {
        Log.d(TAG, "Loading categories from API...");
        
        categoryRepository.getCategories(new CategoryRepository.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                Log.d(TAG, "Categories loaded successfully: " + categories.size() + " items");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (categories != null && !categories.isEmpty()) {
                            categoryList.clear();
                            // Limit to first 8 categories for horizontal scroll
                            int limit = Math.min(categories.size(), 8);
                            categoryList.addAll(categories.subList(0, limit));
                            categoryAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Categories API error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Failed to load categories", 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Categories API failure: " + error);
            }
        });
    }

    private void loadFeaturedProducts() {
        Log.d(TAG, "Loading featured products from API...");
        
        productRepository.getProducts(1, 6, null, new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, ProductResponse.Pagination pagination) {
                Log.d(TAG, "Featured products loaded: " + products.size() + " items");
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        featuredProductList.clear();
                        featuredProductList.addAll(products);
                        productAdapter.notifyDataSetChanged();
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                Log.e(TAG, "Featured products API error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Failed to load featured products", 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Featured products API failure: " + error);
            }
        });
    }

    private void loadPopularProducts() {
        Log.d(TAG, "Loading popular products from API...");
        
        productRepository.getProducts(2, 6, null, new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, ProductResponse.Pagination pagination) {
                Log.d(TAG, "Popular products loaded: " + products.size() + " items");
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        popularProductList.clear();
                        popularProductList.addAll(products);
                        popularProductAdapter.notifyDataSetChanged();
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                Log.e(TAG, "Popular products API error: " + message);
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Popular products API failure: " + error);
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            // If search is empty, reload original data
            loadFeaturedProducts();
            return;
        }
        
        Log.d(TAG, "Performing search for: " + query);
        
        productRepository.getProducts(1, 20, query, new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, ProductResponse.Pagination pagination) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        featuredProductList.clear();
                        featuredProductList.addAll(products);
                        productAdapter.notifyDataSetChanged();
                        
                        if (products.isEmpty()) {
                            Toast.makeText(getContext(), 
                                "No products found for '" + query + "'", 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                Log.e(TAG, "Search error: " + message);
            }
            
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Search failure: " + error);
            }
        });
    }

    private void onCategoryClick(Category category) {
        Log.d(TAG, "Category clicked: " + category.getName());
        
        if (getContext() != null) {
            Intent intent = CategoryProductsActivity.createIntent(getContext(), category);
            startActivity(intent);
        }
    }

    private void onProductClick(Product product) {
        Log.d(TAG, "Product clicked: " + product.getName());
        
        if (getContext() != null) {
            Intent intent = ProductDetailActivity.createIntent(getContext(), product);
            startActivity(intent);
        }
    }
    
    private void onAddToCartClick(Product product) {
        Log.d(TAG, "Add to cart clicked: " + product.getName());
        
        if (getContext() == null) return;
        
        // Check stock availability
        if (!product.isActive() || product.getStockQuantity() <= 0) {
            Toast.makeText(getContext(), 
                "Product is not available", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create request to add 1 item to cart
        AddToCartRequest request = new AddToCartRequest(product.getId(), 1);
        
        Toast.makeText(getContext(), 
            "Adding " + product.getName() + " to cart...", 
            Toast.LENGTH_SHORT).show();
        
        cartRepository.addToCart(request, new CartRepository.CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (cart != null) {
                            Toast.makeText(getContext(), 
                                product.getName() + " added to cart!\nTotal: " + cart.getTotalItems() + " items", 
                                Toast.LENGTH_LONG).show();
                            
                            Log.d(TAG, "Cart updated - Total items: " + cart.getTotalItems() + 
                                  ", Total amount: $" + cart.getTotalAmount());
                        } else {
                            Toast.makeText(getContext(), 
                                product.getName() + " added to cart!", 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Add to cart error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Failed to add to cart: " + message, 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Add to cart failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Cannot connect to server", 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWelcomeMessage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up search handler
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
