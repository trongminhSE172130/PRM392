package com.fpt.project.data.model.response;

import com.fpt.project.data.model.Category;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryResponse {
    private boolean success;
    private int count;
    private List<Category> data;
    private String message;

    // Constructors
    public CategoryResponse() {}

    public CategoryResponse(boolean success, int count, List<Category> data, String message) {
        this.success = success;
        this.count = count;
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 