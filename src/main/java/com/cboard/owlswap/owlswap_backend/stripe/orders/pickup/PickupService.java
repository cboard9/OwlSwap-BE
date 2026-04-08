package com.cboard.owlswap.owlswap_backend.stripe.orders.pickup;

import com.cboard.owlswap.owlswap_backend.dao.OrderDao;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import com.cboard.owlswap.owlswap_backend.stripe.orders.FulfillmentMethod;
import com.cboard.owlswap.owlswap_backend.stripe.orders.Order;
import com.cboard.owlswap.owlswap_backend.stripe.orders.OrderStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PickupService {

    private final OrderDao orderDao;
    private final CurrentUser currentUser;
    private final PickupCodeUtil pickupCodeUtil;

    // how long we reserve an item before it expires
    private static final int RESERVATION_MINUTES = 30;

    public PickupService(OrderDao orderDao,
                        CurrentUser currentUser,
                        PickupCodeUtil pickupCodeUtil) {
        this.orderDao = orderDao;
        this.currentUser = currentUser;
        this.pickupCodeUtil = pickupCodeUtil;
    }


    @Transactional
    public PickupCodeResponseDto generatePickupCode(Integer orderId) {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        if (!order.getBuyer().getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this pickup code.");
        }

        if (order.getFulfillmentMethod() != FulfillmentMethod.PICKUP) {
            throw new BadRequestException("This order is not a pickup order.");
        }

        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new BadRequestException("Pickup code can only be generated for paid pickup orders.");
        }

        String rawCode = pickupCodeUtil.generateRawCode();
        String hash = pickupCodeUtil.hash(rawCode);

        order.setPickupCodeHash(hash);
        order.setPickupCodeGeneratedAt(LocalDateTime.now());
        orderDao.save(order);

        return new PickupCodeResponseDto(order.getOrderId(), rawCode);
    }


    @Transactional
    public Order markReadyForPickup(Integer orderId) {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));


        if (!order.getSeller().getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this order.");
        }

        if (order.getFulfillmentMethod() != FulfillmentMethod.PICKUP) {
            throw new BadRequestException("This order is not a pickup order.");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BadRequestException("Only paid pickup orders can be marked ready.");
        }

        order.setStatus(OrderStatus.READY_FOR_PICKUP);
        order.setReadyForPickupAt(LocalDateTime.now());
        orderDao.save(order);

        return order;
    }

    @Transactional
    public Order confirmPickup(Integer orderId, String pickupCode) {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        if (!order.getSeller().getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to confirm pickup for this order.");
        }

        if (order.getFulfillmentMethod() != FulfillmentMethod.PICKUP) {
            throw new BadRequestException("This order is not a pickup order.");
        }

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new BadRequestException("Pickup can only be confirmed for orders ready for pickup.");
        }

        if (order.getPickupCodeHash() == null || order.getPickupCodeHash().isBlank()) {
            throw new BadRequestException("No pickup code has been generated for this order.");
        }

        if (!pickupCodeUtil.matches(pickupCode, order.getPickupCodeHash())) {
            throw new BadRequestException("Invalid pickup code.");
        }

        order.setStatus(OrderStatus.FULFILLED);
        order.setFulfilledAt(LocalDateTime.now());

        // clear pickup code after successful use
        order.setPickupCodeHash(null);

        orderDao.save(order);

        return order;
    }




}
