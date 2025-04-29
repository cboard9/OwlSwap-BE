package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionDao extends JpaRepository<Transaction, Integer>
{
    List<Transaction> findByBuyer_UserId(int buyerId);
    List<Transaction> findBySeller_UserId(int sellerId);
}
