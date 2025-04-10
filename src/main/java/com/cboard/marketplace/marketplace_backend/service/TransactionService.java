package com.cboard.marketplace.marketplace_backend.service;


import com.cboard.marketplace.marketplace_backend.dao.TransactionDao;
import com.cboard.marketplace.marketplace_backend.model.Transaction;
import com.cboard.marketplace.marketplace_backend.model.User;
import com.cboard.marketplace.marketplace_backend.model.UserItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService
{
    @Autowired
    TransactionDao dao;


    public ResponseEntity<List<Transaction>> getAllTransactions()
    {
        try
        {
            return new ResponseEntity<>(dao.findAll(), HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

}
