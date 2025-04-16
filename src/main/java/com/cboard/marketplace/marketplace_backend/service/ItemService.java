package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.ItemDao;

import com.cboard.marketplace.marketplace_backend.model.DtoMapping.fromDto.DtoToItemFactory;
import com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.marketplace.marketplace_backend.model.Item;
import com.cboard.marketplace.marketplace_backend.model.User;
import com.cboard.marketplace.marketplace_common.ItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ItemService
{
    @Autowired
    ItemDao dao;
    private final ItemToDtoFactory toDtoFactory;
    private final DtoToItemFactory fromDtoFactory;

    //this automatically adds all the different dto mappers through injection
    public ItemService(ItemDao dao, ItemToDtoFactory toDtoFactory, DtoToItemFactory fromDtoFactory)
    {
        this.dao = dao;
        this.toDtoFactory = toDtoFactory;
        this.fromDtoFactory = fromDtoFactory;
    }

    public ResponseEntity<List<ItemDto>> getAllItems()
    {
        List<ItemDto> items = dao.findAll().stream()
                .map(item -> {
                    try
                    {
                        return toDtoFactory.toDto(item);
                    }
                    catch(IllegalAccessException e)
                    {
                        e.printStackTrace();
                        throw new RuntimeException("Error converting item to DTO: " + item, e);
                    }
                        }
                )
                .toList();

        return new ResponseEntity<>(items, HttpStatus.OK);

    }

    public ResponseEntity<User> getItemOwner(int itemId)
    {
        try
        {
            User user = dao.findById(itemId)
                    .map(Item::getUser)
                    .orElseThrow(() -> new NoSuchElementException("User not found for item..."));

            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new User(), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<String> addItem(ItemDto itemDto)
    {
        try
        {
            Item item = fromDtoFactory.fromDto(itemDto);
            if(item.getReleaseDate() == null)
                item.setReleaseDate(String.valueOf(LocalDate.now()));
            dao.save(item);
            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error converting DTO to item: " + itemDto, e);
        }

    }
}
