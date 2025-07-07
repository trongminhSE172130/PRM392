package com.fpt.project.data.model.response;

import com.fpt.project.data.model.Product;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductResponse {
    private boolean success;
    private List<Product> data;
    private Pagination pagination;

    public static class Pagination {
        @SerializedName("current_page")
        private int currentPage;
        
        @SerializedName("total_pages")
        private int totalPages;
        
        @SerializedName("total_items")
        private int totalItems;

        // Getters and Setters
        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<Product> getData() { return data; }
    public void setData(List<Product> data) { this.data = data; }

    public Pagination getPagination() { return pagination; }
    public void setPagination(Pagination pagination) { this.pagination = pagination; }
} 