package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import com.cboard.owlswap.owlswap_backend.model.orders.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDao extends JpaRepository<Order, Integer> {
    List<Order> findByBuyer_UserIdOrderByCreatedAtDesc(Integer buyerId);
    List<Order> findBySeller_UserIdOrderByCreatedAtDesc(Integer sellerId);
    List<Order> findByStatusAndReservedUntilBefore(OrderStatus status, LocalDateTime time);

    Optional<Order> findByCheckoutSessionId(String checkoutSessionId);

}
