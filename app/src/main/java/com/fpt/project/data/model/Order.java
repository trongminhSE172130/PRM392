package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Order {
    @SerializedName("_id")
    private String id;  // MongoDB ObjectId
    
    @SerializedName("order_code")
    private String orderCode;  // Display order number like GH2401234567
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("total_amount")
    private double totalAmount;
    
    private String status; // pending, confirmed, shipped, delivered, cancelled
    
    @SerializedName("payment_status")
    private String paymentStatus; // pending, paid, failed
    
    @SerializedName("shipping_address")
    private ShippingAddressInfo shippingAddress;
    
    @SerializedName("payment_info")
    private PaymentInfo paymentInfo;
    
    private String notes;
    
    @SerializedName("ordered_at")
    private String orderedAt;
    
    @SerializedName("estimated_delivery")
    private String estimatedDelivery;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    private List<OrderItem> items;
    
    private double subtotal;
    
    @SerializedName("shipping_fee")
    private double shippingFee;

    // Constructors
    public Order() {}

    public Order(String userId, double totalAmount, ShippingAddressInfo shippingAddress, String paymentMethod) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.status = "pending";
        this.paymentStatus = "pending";
        
        // Initialize payment info
        this.paymentInfo = new PaymentInfo();
        this.paymentInfo.setMethod(paymentMethod);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public ShippingAddressInfo getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddressInfo shippingAddress) { this.shippingAddress = shippingAddress; }

    public PaymentInfo getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(PaymentInfo paymentInfo) { this.paymentInfo = paymentInfo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getOrderedAt() { return orderedAt; }
    public void setOrderedAt(String orderedAt) { this.orderedAt = orderedAt; }

    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }

    // Convenience methods for backward compatibility
    public String getPaymentMethod() { 
        return paymentInfo != null ? paymentInfo.getMethod() : null; 
    }
    
    public void setPaymentMethod(String paymentMethod) {
        if (paymentInfo == null) {
            paymentInfo = new PaymentInfo();
        }
        paymentInfo.setMethod(paymentMethod);
    }

    // Nested classes for complex objects
    public static class ShippingAddressInfo {
        @SerializedName("full_name")
        private String fullName;
        private String phone;
        private String address;
        private String city;
        @SerializedName("postal_code")
        private String postalCode;
        private String notes;

        // Getters and Setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class PaymentInfo {
        private String method;
        @SerializedName("vnpay_transaction_id")
        private String vnpayTransactionId;
        @SerializedName("vnpay_response_code")
        private String vnpayResponseCode;
        @SerializedName("paid_at")
        private String paidAt;

        // Getters and Setters
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        
        public String getVnpayTransactionId() { return vnpayTransactionId; }
        public void setVnpayTransactionId(String vnpayTransactionId) { this.vnpayTransactionId = vnpayTransactionId; }
        
        public String getVnpayResponseCode() { return vnpayResponseCode; }
        public void setVnpayResponseCode(String vnpayResponseCode) { this.vnpayResponseCode = vnpayResponseCode; }
        
        public String getPaidAt() { return paidAt; }
        public void setPaidAt(String paidAt) { this.paidAt = paidAt; }
    }
}
