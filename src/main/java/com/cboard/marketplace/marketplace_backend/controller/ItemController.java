package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.ItemService;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("item")
public class ItemController
{
    @Autowired
    ItemService service;

    @GetMapping("allItems")
    public ResponseEntity<List<ItemDto>> getAllItems()
    {
        //could potentially throw a runtime exception if cant map an item, consider handling this later?
        return service.getAllItems();
    }

    @PostMapping("add")
    public ResponseEntity<String> addItem(@Valid @RequestBody ItemDto itemDto)
    {
        //could potentially throw a runtime exception if cant map an item, consider handling this later?
        return service.addItem(itemDto);

    }

    @GetMapping("{id}/owner")
    public ResponseEntity<User> getItemOwner(@PathVariable("id") int itemId)
    {
        return service.getItemOwner(itemId);

    }


}
