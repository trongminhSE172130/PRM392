package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Pagination implements Serializable {
    @SerializedName("current_page")
    private int currentPage;
    
    @SerializedName("total_pages")
    private int totalPages;
    
    @SerializedName("total_messages")
    private int totalMessages;
    
    @SerializedName("has_next")
    private boolean hasNext;
    
    @SerializedName("has_prev")
    private boolean hasPrev;
    
    // Constructors
    public Pagination() {}
    
    public Pagination(int currentPage, int totalPages, int totalMessages, boolean hasNext, boolean hasPrev) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalMessages = totalMessages;
        this.hasNext = hasNext;
        this.hasPrev = hasPrev;
    }
    
    // Getters and Setters
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public int getTotalMessages() {
        return totalMessages;
    }
    
    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrev() {
        return hasPrev;
    }
    
    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }
    
    // Utility methods
    public boolean canLoadMore() {
        return hasNext;
    }
    
    public boolean canLoadPrevious() {
        return hasPrev;
    }
    
    public boolean isFirstPage() {
        return currentPage <= 1;
    }
    
    public boolean isLastPage() {
        return currentPage >= totalPages;
    }
    
    @Override
    public String toString() {
        return "Pagination{" +
                "currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", totalMessages=" + totalMessages +
                ", hasNext=" + hasNext +
                ", hasPrev=" + hasPrev +
                '}';
    }
} 