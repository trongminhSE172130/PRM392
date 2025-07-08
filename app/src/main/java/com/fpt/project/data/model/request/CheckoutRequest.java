package com.fpt.project.data.model.request;

public class CheckoutRequest {
    private ShippingAddress shipping_address;
    private String payment_method;
    private String notes;

    public CheckoutRequest() {}

    public CheckoutRequest(ShippingAddress shipping_address, String payment_method, String notes) {
        this.shipping_address = shipping_address;
        this.payment_method = payment_method;
        this.notes = notes;
    }

    // Getters and Setters
    public ShippingAddress getShipping_address() { return shipping_address; }
    public void setShipping_address(ShippingAddress shipping_address) { this.shipping_address = shipping_address; }

    public String getPayment_method() { return payment_method; }
    public void setPayment_method(String payment_method) { this.payment_method = payment_method; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Nested class for shipping address
    public static class ShippingAddress {
        private String full_name;
        private String phone;
        private String address;
        private String city;
        private String postal_code;
        private String notes;

        public ShippingAddress() {}

        public ShippingAddress(String full_name, String phone, String address, String city, String postal_code, String notes) {
            this.full_name = full_name;
            this.phone = phone;
            this.address = address;
            this.city = city;
            this.postal_code = postal_code;
            this.notes = notes;
        }

        // Getters and Setters
        public String getFull_name() { return full_name; }
        public void setFull_name(String full_name) { this.full_name = full_name; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getPostal_code() { return postal_code; }
        public void setPostal_code(String postal_code) { this.postal_code = postal_code; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
} 