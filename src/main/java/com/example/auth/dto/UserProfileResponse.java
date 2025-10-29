package com.example.auth.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String email;
    private String fullName;

    public UserProfileResponse(String email, String fullName) { 
        this.email = email; 
        this.fullName = fullName;
    }
}