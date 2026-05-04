package com.cboard.owlswap.owlswap_backend.service;


import com.cboard.owlswap.owlswap_backend.dao.ItemDao;
import com.cboard.owlswap.owlswap_backend.dao.TransactionDao;
import com.cboard.owlswap.owlswap_backend.dao.UserArchiveDao;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotAvailableException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.TransactionDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.TransactionMapper;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService
{
    @Autowired
    TransactionDao transactionDao;
    @Autowired
    UserArchiveDao userArchiveDao;
    @Autowired
    UserDao userDao;
    @Autowired
    ItemDao itemDao;
    @Autowired
    TransactionMapper transactionMapper;
    @Autowired
    CurrentUser currentUser;



    public List<TransactionDto> getAllTransactions()
    {
        List<Transaction> transactions = transactionDao.findAll();
        List<TransactionDto> dtos = new ArrayList<>();

        return transactions.stream()
                .map(t -> {
                    try {
                        return transactionMapper.transactionToDto(t);
                    } catch (Exception e) {
                        throw new DtoMappingException("Failed to map Transaction to DTO. transactionId=" + t.getTransactionId(), e);
                    }
                })
                .toList();
    }

    public Page<TransactionDto> getAllTransactionsByBuyer(int buyerId, Pageable pageable)
    {
        Page<Transaction> transactions = transactionDao.findByBuyer_UserId(buyerId, pageable);
        return transactions
                .map(t -> {
                            try {
                                return transactionMapper.transactionToDto(t);
                            } catch (Exception e) {
                                throw new DtoMappingException("Failed to map Transaction to DTO. transactionId=" + t.getTransactionId(), e);
                            }
                        }
                );

    }

    public Page<TransactionDto> getAllTransactionsBySeller(int sellerId, Pageable pageable)
    {
        Page<Transaction> transactions = transactionDao.findBySeller_UserId(sellerId, pageable);
        return transactions
                .map(t -> {
                            try {
                                return transactionMapper.transactionToDto(t);
                            } catch (Exception e) {
                                throw new DtoMappingException("Failed to map Transaction to DTO. transactionId=" + t.getTransactionId(), e);
                            }
                        }
                );
    }


    @Transactional
    public void purchaseItem(int itemId)
    {
        Item item = itemDao.findByItemId(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found: " + itemId);
        }

        if (!item.isAvailable()) {
            throw new NotAvailableException("Item is not available.");
        }

        int buyerId = currentUser.userId();

        // prevent buying your own item
        if (item.getUser().getUserId() == buyerId) {
            throw new BadRequestException("You cannot purchase your own item.");
        }

        User buyer = userDao.findById(buyerId)
                .orElseThrow(() -> new NotFoundException("Buyer not found: " + buyerId));

        UserArchive buyerArc = userArchiveDao.findById(buyerId)
                .orElseThrow(() -> new NotFoundException("Buyer archive not found: " + buyerId));

        int sellerId = item.getUser().getUserId();

        UserArchive sellerArc = userArchiveDao.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller archive not found: " + sellerId));

        Transaction sale = new Transaction();
        sale.setItem(item);
        sale.setBuyer(buyerArc);
        sale.setSeller(sellerArc);
        transactionDao.save(sale);

        // transfer ownership + mark unavailable
        item.setUser(buyer);
        item.setAvailable(false);
        itemDao.save(item);

    }
}
