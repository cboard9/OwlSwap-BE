package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.UserItems;
import com.cboard.marketplace.marketplace_backend.service.UserItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user-items")
public class UserItemsController
{
    @Autowired
    UserItemsService service;

    @GetMapping("{id}/items")
    public ResponseEntity<List<UserItems>> getAllUserItems(@PathVariable("id") int userId)
    {
        return service.getAllUserItems(userId);
    }
}
