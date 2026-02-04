package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.model.Dto.TransactionDto;
import com.cboard.owlswap.owlswap_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public ResponseEntity<Page<TransactionDto>> getAllTransactionsByBuyer(@PathVariable("id") int buyerId, @PageableDefault(size=6) Pageable pageable)
    {
        return transactionService.getAllTransactionsByBuyer(buyerId, pageable);
    }

    @GetMapping("seller/{id}/all")
    public ResponseEntity<Page<TransactionDto>> getAllTransactionsBySeller(@PathVariable("id") int sellerId, @PageableDefault(size=6) Pageable pageable)
    {
        return transactionService.getAllTransactionsBySeller(sellerId, pageable);
    }


    @PostMapping("purchase/{itemId}/{buyerId}")
    public ResponseEntity<String> purchaseItem(@PathVariable("itemId") int itemId, @PathVariable("buyerId") int buyerId)
    {
        return transactionService.purchaseItem(itemId, buyerId);
    }




}
