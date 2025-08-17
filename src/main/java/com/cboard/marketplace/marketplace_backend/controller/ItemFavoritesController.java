package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.Dto.ItemDto;
import com.cboard.marketplace.marketplace_backend.model.ItemFavorite;
import com.cboard.marketplace.marketplace_backend.service.ItemFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


import java.util.List;

@RestController
@RequestMapping("item-favorites")
public class ItemFavoritesController
{
    @Autowired
    ItemFavoritesService service;

    @GetMapping("item/{id}")
    public ResponseEntity<List<ItemFavorite>> getItemFavoritesByItem(@PathVariable("id") int itemId)
    {
        return service.getItemFavoritesByItem(itemId);
    }
    @GetMapping("user/{id}")
    public ResponseEntity<Page<ItemDto>> getItemFavoritesByUser(@PathVariable("id") int userId, @PageableDefault(size = 6) Pageable pageable)
    {
        return service.getItemFavoritesByUser(userId, pageable);
    }

    @PostMapping("add/user{userId}/item{itemId}")
    public ResponseEntity<String> addFavorite(@PathVariable("userId") int userId, @PathVariable("itemId") int itemId)
    {
        return service.addFavorite(userId, itemId);
    }

    @DeleteMapping("delete/user{userId}/item{itemId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable("userId") int userId, @PathVariable("itemId") int itemId)
    {
        return service.deleteFavorite(userId, itemId);
    }
}
