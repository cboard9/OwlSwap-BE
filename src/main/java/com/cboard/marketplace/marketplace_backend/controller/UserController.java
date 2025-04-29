package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.dao.UserDao;
import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.RatingService;
import com.cboard.marketplace.marketplace_backend.service.UserService;
import com.cboard.marketplace.marketplace_common.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<UserDto> getProfile() {
        //retrieve authenticated username directly
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();
        User user = userDao.findByUsername(username).orElseThrow();

        Double avgRating = ratingService.calculateAverageRating(user.getUserId());
        user.setAverageRating(avgRating);

        // convert user to userDto
        UserDto userDto = new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getAverageRating()
        );

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/api/rate/{userId}")
    public ResponseEntity<String> rateUser(@PathVariable int userId, @RequestParam double score) {
        return ratingService.addRating(userId, score);
    }
}