package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.UserDto;
import com.cboard.owlswap.owlswap_backend.service.RatingService;
import com.cboard.owlswap.owlswap_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        return service.getProfile();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") int userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Double avgRating = ratingService.calculateAverageRating(user.getUserId());
        user.setAverageRating(avgRating);

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
    public ResponseEntity<String> rateUser(@PathVariable int userId, @RequestBody double rating) {
        return ratingService.addRating(userId, rating);
    }

    @DeleteMapping("{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId)
    {
        return service.deleteUser(userId);
    }
}