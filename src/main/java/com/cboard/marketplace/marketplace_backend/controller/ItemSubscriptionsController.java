package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.ItemSubscriptions;
import com.cboard.marketplace.marketplace_backend.service.ItemSubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("item-subscriptions")
public class ItemSubscriptionsController
{
    @Autowired
    ItemSubscriptionsService service;

    @GetMapping("{id}/subscriptions")
    public ResponseEntity<List<ItemSubscriptions>> getAllItemSubscriptions(@PathVariable("id") int itemId)
    {
        return service.getAllItemSubscriptions(itemId);
    }
}
