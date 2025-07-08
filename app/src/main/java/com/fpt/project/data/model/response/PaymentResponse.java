package com.fpt.project.data.model.response;

public class PaymentResponse {
    private boolean success;
    private PaymentData data;
    private String message;

    public PaymentResponse() {}

    public PaymentResponse(boolean success, PaymentData data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public PaymentData getData() { return data; }
    public void setData(PaymentData data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // Nested class for payment data
    public static class PaymentData {
        private String payment_url;

        public PaymentData() {}

        public PaymentData(String payment_url) {
            this.payment_url = payment_url;
        }

        public String getPayment_url() { return payment_url; }
        public void setPayment_url(String payment_url) { this.payment_url = payment_url; }
    }
} 