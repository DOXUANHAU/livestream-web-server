package com.example.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LoginResponse;
import com.example.auth.dto.RegisterNewAccountRequest;
import com.example.auth.service.AuthService;
import com.example.auth.service.JwtService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;


    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterNewAccountRequest request) {
        ApiResponse<?> response = authService.register(request);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 400)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        ApiResponse<LoginResponse> response = authService.login(request);
        return ResponseEntity
                .status(response.isSuccess() ? 200 : 401)
                .body(response);
    }

    // @GetMapping("/profile")
    // public ResponseEntity<UserProfileResponse> profile(@RequestHeader("Authorization") String authHeader) {
    //     String token = authHeader.replace("Bearer ", "");
    //     String email = jwtService.extractEmail(token); 
    //     return ResponseEntity.ok(authService.getProfile(email));
    // }


    // @PostMapping("/logout")
    // public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
    //     String token = authHeader.replace("Bearer ", "");
    //     authService.logout(token);
    //     return ResponseEntity.ok().build();
    // }
}