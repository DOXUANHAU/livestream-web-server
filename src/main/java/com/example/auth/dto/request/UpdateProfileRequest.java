package com.example.auth.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String email;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String streamerName;
}