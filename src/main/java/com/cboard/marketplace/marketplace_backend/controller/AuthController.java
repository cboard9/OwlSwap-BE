package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.dto.LoginRequest;
import com.cboard.marketplace.marketplace_backend.dto.SignupRequest;
import com.cboard.marketplace.marketplace_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        return authService.loginUser(request);
    }
}