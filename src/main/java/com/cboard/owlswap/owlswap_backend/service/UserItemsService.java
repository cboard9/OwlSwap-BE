package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.UserItemsDao;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.UserItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserItemsService
{
    @Autowired
    UserItemsDao dao;
    @Autowired
    ItemToDtoFactory toDtoFactory;

/*    public ResponseEntity<List<UserItems>> getAllUserItems(int userId)
    {
        try
        {
            *//* only return the items, no user info?
            List<UserItems> userItems = userItemsDao.findByUserUserId(userId);
            List<Item> items = userItems.stream()
                    .map(UserItems::getItem)
                    .toList();
                    *//*

            List<UserItems> items = dao.findByUserUserId(userId);
            return new ResponseEntity<>(items, HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }*/

    public ResponseEntity<List<ItemDto>> getAllUserItems(int userId)
    {
        try
        {
            //only return the items, no user info?
            List<UserItems> userItems = dao.findByUserUserId(userId);
            List<ItemDto> items = userItems.stream()
                    .map(UserItems::getItem)
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
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }
}
