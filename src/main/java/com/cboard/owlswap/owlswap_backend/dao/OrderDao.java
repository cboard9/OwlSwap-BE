package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDao extends JpaRepository<Order, Integer> {
    List<Order> findByBuyer_UserIdOrderByCreatedAtDesc(Integer buyerId);
    List<Order> findBySeller_UserIdOrderByCreatedAtDesc(Integer sellerId);
}
