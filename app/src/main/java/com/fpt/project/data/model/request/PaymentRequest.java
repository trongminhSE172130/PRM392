package com.fpt.project.data.model.request;

public class PaymentRequest {
    private String return_url;

    public PaymentRequest() {}

    public PaymentRequest(String return_url) {
        this.return_url = return_url;
    }

    // Getters and Setters
    public String getReturn_url() { return return_url; }
    public void setReturn_url(String return_url) { this.return_url = return_url; }
} 