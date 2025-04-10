package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.ItemSubscriptionsDao;
import com.cboard.marketplace.marketplace_backend.model.ItemSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemSubscriptionsService
{
    @Autowired
    ItemSubscriptionsDao dao;

    public ResponseEntity<List<ItemSubscriptions>> getAllItemSubscriptions(int itemId)
    {
        try
        {
            List<ItemSubscriptions> itemSubscriptions = dao.findByItemItemId(itemId);
            return new ResponseEntity<>(itemSubscriptions, HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }
}
