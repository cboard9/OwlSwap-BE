package com.cboard.marketplace.marketplace_backend.controller;

import com.cboard.marketplace.marketplace_backend.model.*;

import com.cboard.marketplace.marketplace_backend.service.TransactionService;
import com.cboard.marketplace.marketplace_common.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController
{
    @Autowired
    TransactionService transactionService;

    @GetMapping("all")
    public ResponseEntity<List<TransactionDto>> getAllTransactions()
    {
        return transactionService.getAllTransactions();
    }

    @GetMapping("buyer/{id}/all")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByBuyer(@PathVariable("id") int buyerId)
    {
        return transactionService.getAllTransactionsByBuyer(buyerId);
    }

    @GetMapping("seller/{id}/all")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsBySeller(@PathVariable("id") int sellerId)
    {
        return transactionService.getAllTransactionsBySeller(sellerId);
    }


    @PostMapping("purchase/{itemId}/{buyerId}")
    public ResponseEntity<String> purchaseItem(@PathVariable("itemId") int itemId, @PathVariable("buyerId") int buyerId)
    {
        return transactionService.purchaseItem(itemId, buyerId);
    }




}
