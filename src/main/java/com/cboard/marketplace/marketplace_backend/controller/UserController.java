package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.dao.UserDao;
import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.ItemService;
import com.cboard.marketplace.marketplace_backend.service.RatingService;
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

    @Autowired
    RatingService ratingService;

    @GetMapping("allUsers")
    public ResponseEntity<List<User>> getAllUsers()
    {
        return service.getAllUsers();
    }

    @GetMapping("/api/profile")
    public ResponseEntity<User> getProfile() {
        //retrieve authenticated username directly
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();
        User user = userDao.findByUsername(username).orElseThrow();
        user.setPassword(null);

        Double avgRating = ratingService.calculateAverageRating(user.getUserId());
        user.setAverageRating(avgRating);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/api/rate/{userId}")
    public ResponseEntity<String> rateUser(@PathVariable int userId, @RequestParam double score) {
        return ratingService.addRating(userId, score);
    }
}