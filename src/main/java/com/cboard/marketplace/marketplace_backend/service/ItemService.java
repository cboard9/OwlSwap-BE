package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.ItemDao;

import com.cboard.marketplace.marketplace_backend.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService
{
    @Autowired
    ItemDao dao;

    //ResponseEntity<List<Item>>
    public List<Item> getAllItems()
    {
        return dao.findAll();

        /*try
        {
            return new ResponseEntity<>(dao.findAll(), HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);*/
    }
}
