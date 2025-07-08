package com.fpt.project.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.Category;
import com.fpt.project.data.repository.CategoryRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";

    // Views
    private RecyclerView recyclerViewCategories;
    private View layoutLoading;
    private View layoutEmpty;
    private View layoutError;
    private TextView tvErrorMessage;
    private MaterialButton btnRetry;
    private MaterialButton btnRetryError;

    // Data
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private CategoryRepository categoryRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadCategories();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh categories when fragment becomes visible again
        if (categoryList.isEmpty()) {
            loadCategories();
        }
    }

    private void initViews(View view) {
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);
        btnRetryError = view.findViewById(R.id.btnRetryError);
        
        categoryRepository = new CategoryRepository(getContext());
    }

    private void setupRecyclerView() {
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, this::onCategoryClick);
        
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewCategories.setLayoutManager(layoutManager);
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void setupClickListeners() {
        btnRetry.setOnClickListener(v -> loadCategories());
        btnRetryError.setOnClickListener(v -> loadCategories());
    }

    private void loadCategories() {
        Log.d(TAG, "Loading categories from API...");
        showLoading(true);
        
        categoryRepository.getCategories(new CategoryRepository.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                Log.d(TAG, "Categories loaded successfully: " + categories.size() + " items");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        
                        if (categories != null && !categories.isEmpty()) {
                            categoryList.clear();
                            categoryList.addAll(categories);
                            categoryAdapter.notifyDataSetChanged();
                            showContent(true);
                            
                            Toast.makeText(getContext(), 
                                "Loaded " + categories.size() + " categories successfully!", 
                                Toast.LENGTH_SHORT).show();
                        } else {
                            showEmpty(true);
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Categories API error: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showError(true, message);
                        
                        // Show less intrusive error message
                        Toast.makeText(getContext(), 
                            "Failed to load categories from server", 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Categories API failure: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showError(true, "Network connection failed. Please check your internet connection.");
                        
                        Toast.makeText(getContext(), 
                            "No internet connection", 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewCategories.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showContent(boolean show) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewCategories.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showEmpty(boolean show) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewCategories.setVisibility(View.GONE);
        layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showError(boolean show, String errorMessage) {
        layoutLoading.setVisibility(View.GONE);
        recyclerViewCategories.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(show ? View.VISIBLE : View.GONE);
        
        if (show && tvErrorMessage != null) {
            tvErrorMessage.setText(errorMessage != null ? errorMessage : "Unknown error occurred");
        }
    }

    private void onCategoryClick(Category category) {
        Log.d(TAG, "Category clicked: " + category.getName());
        
        if (getContext() != null) {
            Intent intent = CategoryProductsActivity.createIntent(getContext(), category);
            startActivity(intent);
        }
    }
} 