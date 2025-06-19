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
import com.fpt.project.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

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
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this::onProductClick);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        // TODO: Load products from API
        // For now, add dummy data
        loadDummyProducts();
    }

    private void loadDummyProducts() {
        productList.clear();
        
        // Add some dummy products
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId(i);
            product.setName("Product " + i);
            product.setDescription("Description for product " + i);
            product.setPrice(19.99 + (i * 5));
            product.setStockQuantity(50 + i);
            product.setImageUrl("https://via.placeholder.com/300x300?text=Product+" + i);
            product.setActive(true);
            productList.add(product);
        }
        
        productAdapter.notifyDataSetChanged();
    }

    private void onProductClick(Product product) {
        // TODO: Navigate to product details
        // For now, show a toast
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "Clicked on " + product.getName(), 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
