package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionDao extends JpaRepository<Transaction, Integer>
{
    Page<Transaction> findByBuyer_UserId(int buyerId, Pageable pageable);
    Page<Transaction> findBySeller_UserId(int sellerId, Pageable pageable);
}
