package com.fpt.project.data.model;

public class StoreInfo {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String phone;
    private String email;
    private String openingHours;
    private String description;
    private String createdAt;

    // Constructors
    public StoreInfo() {}

    public StoreInfo(String name, String address, double latitude, double longitude, 
                     String phone, String email, String openingHours, String description) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.email = email;
        this.openingHours = openingHours;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
