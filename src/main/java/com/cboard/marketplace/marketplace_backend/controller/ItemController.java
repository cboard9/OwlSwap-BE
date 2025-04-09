package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.Item;
import com.cboard.marketplace.marketplace_backend.service.ItemService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("item")
public class ItemController
{
    @Autowired
    ItemService service;

    @GetMapping("allItems")
    public ResponseEntity<List<Item>> getAllItems()
    {
        return service.getAllItems();
    }


}
