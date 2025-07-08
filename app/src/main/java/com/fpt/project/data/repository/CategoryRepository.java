package com.fpt.project.data.repository;

import android.content.Context;
import android.util.Log;

import com.fpt.project.data.model.Category;
import com.fpt.project.data.model.response.CategoryResponse;
import com.fpt.project.data.network.NetworkClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    
    private static final String TAG = "CategoryRepository";
    private Context context;
    
    public CategoryRepository(Context context) {
        this.context = context;
    }
    
    public interface CategoryListCallback {
        void onSuccess(List<Category> categories);
        void onError(String message);
        void onFailure(String error);
    }
    
    public void getCategories(CategoryListCallback callback) {
        Call<CategoryResponse> call = NetworkClient.getInstance(context).getApiService().getCategories();
        
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    CategoryResponse categoryResponse = response.body();
                    if (categoryResponse != null && categoryResponse.isSuccess()) {
                        Log.d(TAG, "Categories loaded successfully: " + categoryResponse.getCount() + " items");
                        callback.onSuccess(categoryResponse.getData());
                    } else {
                        String message = categoryResponse != null ? categoryResponse.getMessage() : "Unknown error";
                        Log.e(TAG, "Categories API error: " + message);
                        callback.onError(message);
                    }
                } else {
                    String error = "HTTP " + response.code() + ": " + response.message();
                    Log.e(TAG, "Categories API HTTP error: " + error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, "Categories API network failure: " + error);
                callback.onFailure(error);
            }
        });
    }
} 