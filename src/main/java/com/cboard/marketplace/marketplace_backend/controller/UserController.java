package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.ItemService;

import com.cboard.marketplace.marketplace_backend.service.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController
{
    @Autowired
    UserService service;

    @GetMapping("allUsers")
    public ResponseEntity<List<User>> getAllUsers()
    {
        return service.getAllUsers();
    }

    @GetMapping("{id}/items")
    public ResponseEntity<List<UserItems>> getAllUserItems(@PathVariable("id") int userId)
    {
        return service.getAllUserItems(userId);
    }


}
