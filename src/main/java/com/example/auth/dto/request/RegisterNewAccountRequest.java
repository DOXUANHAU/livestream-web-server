package com.example.auth.dto.request;

import lombok.Data;

@Data
public class RegisterNewAccountRequest {
    private String email;
    private String password;
    private String fullName;
}