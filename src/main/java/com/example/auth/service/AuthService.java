package com.example.auth.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegisterNewAccountRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.LoginResponse;
import com.example.auth.dto.response.UserResponse;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    public ApiResponse<UserResponse> register(RegisterNewAccountRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ApiResponse<>(false, "Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());

        User savedUser = userRepository.save(user);

        UserResponse userResponse = new UserResponse(savedUser.getEmail(), savedUser.getFullName());

        return new ApiResponse<>(true, "Register successful", userResponse);
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return new ApiResponse<>(false, "Invalid email or password");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ApiResponse<>(false, "Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());
        LoginResponse response = new LoginResponse(token, user.getEmail(), user.getFullName());

        return new ApiResponse<>(true, "Login successful", response);
    }


    // public UserProfileResponse getProfile(String email) {
    //     User user = userRepository.findByEmail(email)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
    //     return new UserProfileResponse(user.getEmail(), user.getFullName());
    // }
}
