package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.model.Dto.AuthResponse;
import com.cboard.owlswap.owlswap_backend.model.Dto.LoginRequest;
import com.cboard.owlswap.owlswap_backend.model.Dto.SignupRequest;
import com.cboard.owlswap.owlswap_backend.security.JwtUtil;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.model.User;
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

        if(request.getUsername() == null || request.getUsername().isBlank() || request.getPassword() == null || request.getPassword().isBlank())
            return ResponseEntity.badRequest().body("Username and password can not be blank.");

        if(request.getFirstName() == null || request.getFirstName().isBlank() || request.getLastName() == null || request.getLastName().isBlank())
            return ResponseEntity.badRequest().body("Must enter first and last name.");

        if (userDao.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        if(request.getEmail() == null || request.getEmail().isBlank())
            return ResponseEntity.badRequest().body("Must enter an email.");

        if (!request.getEmail().toLowerCase().endsWith("@westfield.ma.edu")) {
            return ResponseEntity.badRequest().body("Email must be a Westfield student address.");
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

            if(request.getUsername() == null || request.getUsername().isBlank() || request.getPassword() == null || request.getPassword().isBlank())
                return ResponseEntity.badRequest().body("Username and password can not be blank.");

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            Optional<User> user = userDao.findByUsername(request.getUsername());
            if (user.isEmpty())
                return ResponseEntity.status(400).body("Username not found.");

            String token = jwtUtil.generateToken(user.get());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }
    }
}