package com.fpt.project.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.Cart;
import com.fpt.project.data.model.Product;
import com.fpt.project.data.model.request.AddToCartRequest;
import com.fpt.project.data.model.response.ProductResponse;
import com.fpt.project.data.repository.CartRepository;
import com.fpt.project.data.repository.ProductRepository;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProductRepository productRepository;
    private CartRepository cartRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupRecyclerView();
        loadProducts();
        return view;
    }

    private void initViews(View view) {
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        productRepository = new ProductRepository(getContext());
        cartRepository = new CartRepository(getContext());
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this::onProductClick, this::onAddToCartClick);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        // Load products from API
        loadProductsFromAPI();
    }
    
    private void loadProductsFromAPI() {
        Log.d("HomeFragment", "Loading products from API...");
        
        productRepository.getProducts(1, 20, null, new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, ProductResponse.Pagination pagination) {

                // Log first product details for debugging
                if (!products.isEmpty()) {
                    Product firstProduct = products.get(0);
                    Log.d("HomeFragment", "First product: " + firstProduct.getName() + 
                          ", Price: " + firstProduct.getPrice() + 
                          ", Category: " + firstProduct.getCategoryName() +
                          ", SKU: " + firstProduct.getSku() +
                          ", Stock: " + firstProduct.getStockQuantity() +
                          ", Primary Image: " + firstProduct.getPrimaryImageUrl());
                }
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        productList.clear();
                        productList.addAll(products);
                        productAdapter.notifyDataSetChanged();
                        
                        android.widget.Toast.makeText(getContext(), 
                            "Loaded " + products.size() + " products successfully!", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    });
                }
            }
            
            @Override
            public void onError(String message) {
                Log.e("HomeFragment", "Products API error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(), 
                            "Failed to load products: " + message, 
                            android.widget.Toast.LENGTH_SHORT).show();
                        
                        // Load dummy data as fallback
                        loadDummyProducts();
                    });
                }
            }
            
            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Products API failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(), 
                            "Cannot connect to server. Loading offline data...", 
                            android.widget.Toast.LENGTH_LONG).show();
                        
                        // Load dummy data as fallback
                        loadDummyProducts();
                    });
                }
            }
        });
    }

    private void loadDummyProducts() {
        productList.clear();
        
        // Add some dummy products
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId("dummy_" + i);
            product.setName("Product " + i);
            product.setDescription("Description for product " + i);
            product.setPrice(19.99 + (i * 5));
            product.setStockQuantity(50 + i);
            product.setSku("DUMMY-" + i);
            product.setActive(true);
            product.setFeatured(i % 3 == 0); // Every 3rd product is featured
            productList.add(product);
        }
        
        productAdapter.notifyDataSetChanged();
    }

    private void onProductClick(Product product) {
        Log.d("HomeFragment", "Product clicked: " + product.getName());
        
        if (getContext() != null) {
            // Navigate to ProductDetailActivity
            android.content.Intent intent = ProductDetailActivity.createIntent(getContext(), product);
            startActivity(intent);
        }
    }
    
    private void onAddToCartClick(Product product) {
        Log.d("HomeFragment", "Add to cart clicked: " + product.getName());
        
        if (getContext() == null) return;
        
        // Check stock availability
        if (!product.isActive() || product.getStockQuantity() <= 0) {
            android.widget.Toast.makeText(getContext(), 
                "Product is not available", 
                android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create request to add 1 item to cart
        AddToCartRequest request = new AddToCartRequest(product.getId(), 1);
        
        android.widget.Toast.makeText(getContext(), 
            "Adding " + product.getName() + " to cart...", 
            android.widget.Toast.LENGTH_SHORT).show();
        
        cartRepository.addToCart(request, new CartRepository.CartCallback<Cart>() {
            @Override
            public void onSuccess(Cart cart) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (cart != null) {
                            android.widget.Toast.makeText(getContext(), 
                                product.getName() + " added to cart!\nTotal: " + cart.getTotalItems() + " items", 
                                android.widget.Toast.LENGTH_LONG).show();
                            
                            Log.d("HomeFragment", "Cart updated - Total items: " + cart.getTotalItems() + 
                                  ", Total amount: $" + cart.getTotalAmount());
                        } else {
                            android.widget.Toast.makeText(getContext(), 
                                product.getName() + " added to cart!", 
                                android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                Log.e("HomeFragment", "Add to cart error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(), 
                            "Failed to add to cart: " + message, 
                            android.widget.Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Add to cart failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(), 
                            "Network error. Please try again.", 
                            android.widget.Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}
