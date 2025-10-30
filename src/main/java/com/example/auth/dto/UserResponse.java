package com.example.auth.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String fullName;

    public UserResponse(String email, String fullName) { 
        this.email = email; 
        this.fullName = fullName;
    }
}