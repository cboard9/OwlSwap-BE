package com.cboard.owlswap.owlswap_backend.controller;


import com.cboard.owlswap.owlswap_backend.model.Dto.LoginRequest;
import com.cboard.owlswap.owlswap_backend.model.Dto.SignupRequest;
import com.cboard.owlswap.owlswap_backend.service.AuthService;
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