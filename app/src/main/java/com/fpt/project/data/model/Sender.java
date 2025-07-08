package com.fpt.project.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Sender implements Serializable {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("full_name")
    private String fullName;
    
    // Constructors
    public Sender() {}
    
    public Sender(String id, String email) {
        this.id = id;
        this.email = email;
    }
    
    // Getters and Setters
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getFullName() { 
        return fullName; 
    }
    
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }
} 