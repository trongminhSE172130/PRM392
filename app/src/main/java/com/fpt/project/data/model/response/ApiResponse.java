package com.fpt.project.data.model.response;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Pagination pagination;

    // Constructors
    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Pagination getPagination() { return pagination; }
    public void setPagination(Pagination pagination) { this.pagination = pagination; }

    public static class Pagination {
        private int page;
        private int limit;
        private int total;
        private int totalPages;

        // Getters and Setters
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }

        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
