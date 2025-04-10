package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.ItemService;

import com.cboard.marketplace.marketplace_backend.service.TransactionService;
import com.cboard.marketplace.marketplace_backend.service.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController
{
    @Autowired
    TransactionService service;

    @GetMapping("allTransactions")
    public ResponseEntity<List<Transaction>> getAllTransactions()
    {
        return service.getAllTransactions();
    }




}
