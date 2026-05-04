package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.UserDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.UserMapper;
import com.cboard.owlswap.owlswap_backend.service.RatingService;
import com.cboard.owlswap.owlswap_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Autowired
    UserMapper userMapper;


    @GetMapping("profile")
    public ResponseEntity<UserDto> getProfile() {
        UserDto user = service.getProfile();
        return ResponseEntity.ok(user);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") int userId) {
        UserDto user = service.getUserById(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("rate/{id}")
    public ResponseEntity<String> rateUser(@PathVariable("id") int userId,
                                           @Valid @RequestBody RatingRequest req)
    {
        ratingService.addRating(userId, req.rating());
        return ResponseEntity.status(HttpStatus.CREATED).body("Rating successfully added");
    }

    @DeleteMapping("{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId)
    {
        service.deleteUser(userId);
        return ResponseEntity.ok("User deleted.");
    }

}