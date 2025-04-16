package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.dao.UserDao;
import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.ItemService;
import com.cboard.marketplace.marketplace_backend.service.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController
{
    @Autowired
    UserService service;

    @Autowired
    UserDao userDao;

    @GetMapping("allUsers")
    public ResponseEntity<List<User>> getAllUsers()
    {
        return service.getAllUsers();
    }

    @GetMapping("/api/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        // debug
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Current authenticated user: " + auth.getName());

        String username = userDetails.getUsername();
        User user = userDao.findByUsername(username).orElseThrow();
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}