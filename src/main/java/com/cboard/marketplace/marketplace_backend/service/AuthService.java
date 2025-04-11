package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.security.JwtUtil;
import com.cboard.marketplace.marketplace_backend.dao.UserDao;
import com.cboard.marketplace.marketplace_backend.dto.AuthResponse;
import com.cboard.marketplace.marketplace_backend.dto.LoginRequest;
import com.cboard.marketplace.marketplace_backend.dto.SignupRequest;
import com.cboard.marketplace.marketplace_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> registerUser(SignupRequest request) {
        if (!request.getEmail().toLowerCase().endsWith("@westfield.ma.edu")) {
            return ResponseEntity.badRequest().body("Email must be a Westfield student address.");
        }

        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body("Username already exists.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userDao.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    public ResponseEntity<?> loginUser(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            Optional<User> user = userDao.findByUsername(request.getUsername());
            if (user.isEmpty())
                return ResponseEntity.status(401).body("Invalid credentials");

            String token = jwtUtil.generateToken(user.get());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }
    }
}