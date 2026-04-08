package com.cboard.owlswap.owlswap_backend.service;


import com.cboard.owlswap.owlswap_backend.dao.*;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotAvailableException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.OrderDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.TransactionDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.OrderToDtoMapper;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.TransactionMapper;
import com.cboard.owlswap.owlswap_backend.model.orders.ListingStatus;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import com.cboard.owlswap.owlswap_backend.model.orders.OrderStatus;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import com.cboard.owlswap.owlswap_backend.stripe.webhook.StripeWebhookService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService
{
    private final OrderDao orderDao;
    private final ItemDao itemDao;
    private final UserArchiveDao userArchiveDao;
    private final CurrentUser currentUser;
    private final OrderToDtoMapper orderToDtoMapper;
    private final StripeWebhookService stripeWebhookService;

    // how long we reserve an item before it expires
    private static final int RESERVATION_MINUTES = 1;

    public OrderService(OrderDao orderDao,
                        ItemDao itemDao,
                        UserArchiveDao userArchiveDao,
                        CurrentUser currentUser,
                        OrderToDtoMapper orderToDtoMapper,
                        StripeWebhookService stripeWebhookService) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
        this.userArchiveDao = userArchiveDao;
        this.currentUser = currentUser;
        this.orderToDtoMapper = orderToDtoMapper;
        this.stripeWebhookService = stripeWebhookService;
    }

    @Transactional
    public Order createOrderAndReserveItem(Integer itemId)
    {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer buyerId = currentUser.userId();
        UserArchive buyer = userArchiveDao.findById(buyerId)
                .orElseThrow(() -> new NotFoundException("Buyer not found."));

        // Lock the item row so two buyers can't reserve at the same time
        Item item = itemDao.findByIdForUpdate(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found."));

        // Cannot buy your own item
        if (item.getUser() != null && item.getUser().getUserId().equals(buyerId)) {
            throw new BadRequestException("You cannot purchase your own item.");
        }

        // If item is not available, block purchase
        if (item.getListingStatus() != ListingStatus.AVAILABLE) {
            throw new NotAvailableException("Item is not available.");
        }

        // Seller (from Item.owner) - stored as snapshot on order
        Integer sellerId = item.getUser().getUserId();
        UserArchive seller = userArchiveDao.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found."));

        // Create order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setItem(item);

        // Freeze amount NOW
        order.setAmount(BigDecimal.valueOf(item.getPrice()));
        order.setCurrency("USD");
        order.setStatus(OrderStatus.PENDING);

        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(RESERVATION_MINUTES);
        order.setReservedUntil(reservedUntil);

        Order saved = orderDao.save(order);

        // Reserve item
        item.setListingStatus(ListingStatus.RESERVED);
        item.setReservedUntil(reservedUntil);
        item.setReservedByOrder(saved);

        // Optional: keep legacy available in sync (for now)
        item.setAvailable(false);

        itemDao.save(item);

        return saved;
    }


    @Transactional
    public Order cancelOrder(Integer orderId)
    {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        // Only buyer can cancel (admin could later)
        if (!order.getBuyer().getUserId().equals(userId)) {
            throw new AccessDeniedException("You cannot cancel this order.");
        }

        // Only cancel pending orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be cancelled.");
        }

        // Expire live Stripe checkout session first, if one exists
        try {
            stripeWebhookService.expireCheckoutSessionIfOpenInternal(order);
        } catch (StripeException e) {
            throw new BadRequestException("Failed to expire Stripe checkout session.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCheckoutSessionId(null);
        order.setLatestPaymentStatus(null);
        orderDao.save(order);

        // Release item if this order is the reserver
        Item item = itemDao.findByIdForUpdate(order.getItem().getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found."));

        if (item.getReservedByOrder() != null
                && item.getReservedByOrder().getOrderId().equals(order.getOrderId()))
        {

            item.setListingStatus(ListingStatus.AVAILABLE);
            item.setReservedByOrder(null);
            item.setReservedUntil(null);
            item.setAvailable(true); // legacy
            itemDao.save(item);
        }

        return order;
    }


    // TEMPORARY: do this now to test end-to-end without Stripe.
    // Later: Stripe webhook sets PAID.
    @Transactional
    public Order markPaid(Integer orderId)
    {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        // only buyer can mark paid (temporary)
        if (!order.getBuyer().getUserId().equals(userId)) {
            throw new AccessDeniedException("You cannot pay for this order.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is not pending.");
        }

        // Validate reservation not expired
        if (order.getReservedUntil() != null && order.getReservedUntil().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Order reservation expired.");
        }

        // mark order paid
        order.setStatus(OrderStatus.PAID);
        orderDao.save(order);

        // mark item sold
        Item item = itemDao.findByIdForUpdate(order.getItem().getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found."));

        item.setListingStatus(ListingStatus.SOLD);
        item.setReservedUntil(null);
        item.setReservedByOrder(null);
        item.setAvailable(false); // legacy
        itemDao.save(item);

        return order;
    }


    @Transactional
    public Order fulfill(Integer orderId)
    {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        // only seller can fulfill
        if (!order.getSeller().getUserId().equals(userId)) {
            throw new AccessDeniedException("You cannot fulfill this order.");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BadRequestException("Only paid orders can be fulfilled.");
        }

        order.setStatus(OrderStatus.FULFILLED);
        return orderDao.save(order);
    }


    public List<OrderDto> getAllOrdersByBuyer(int buyerId)
    {
        List<Order> orders = orderDao.findByBuyer_UserIdOrderByCreatedAtDesc(buyerId);

        return orders.stream()
                .map(o -> {
                    try
                    {
                        return orderToDtoMapper.toDto(o);
                    }
                    catch(Exception e)
                    {
                        throw new DtoMappingException("Failed to map Order to DTO. orderId=" + o.getOrderId(), e);
                    }
                })
                .toList();
    }

    public List<OrderDto> getAllOrdersBySeller(int sellerId)
    {
        List<Order> orders = orderDao.findBySeller_UserIdOrderByCreatedAtDesc(sellerId);

        return orders.stream()
                .map(o -> {
                    try
                    {
                        return orderToDtoMapper.toDto(o);
                    }
                    catch(Exception e)
                    {
                        throw new DtoMappingException("Failed to map Order to DTO. orderId=" + o.getOrderId(), e);
                    }
                })
                .toList();
    }

    public List<OrderDto> getAllOrders()
    {
        List<Order> orders = orderDao.findAll();

        return orders.stream()
                .map(o -> {
                    try
                    {
                        return orderToDtoMapper.toDto(o);
                    }
                    catch(Exception e)
                    {
                        throw new DtoMappingException("Failed to map Order to DTO. orderId=" + o.getOrderId(), e);
                    }
                })
                .toList();
    }

    public OrderDto getOrderById(int orderId)
    {
        Order order = orderDao.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("No order found by order id=" + orderId));

        try
        {
            return orderToDtoMapper.toDto(order);
        }
        catch (Exception e)
        {
            throw new DtoMappingException("Failed to map Order to DTO. orderId=" + orderId, e);
        }


    }

    public List<OrderDto> getMyPurchases()
    {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        List<Order> orders = orderDao.findByBuyer_UserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(o -> {
                    try
                    {
                        return orderToDtoMapper.toDto(o);
                    }
                    catch(Exception e)
                    {
                        throw new DtoMappingException("Failed to map Order to DTO. orderId=" + o.getOrderId(), e);
                    }
                })
                .toList();

    }

    public List<OrderDto> getMySales()
    {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        List<Order> orders = orderDao.findBySeller_UserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(o -> {
                    try
                    {
                        return orderToDtoMapper.toDto(o);
                    }
                    catch(Exception e)
                    {
                        throw new DtoMappingException("Failed to map Order to DTO. orderId=" + o.getOrderId(), e);
                    }
                })
                .toList();
    }
}
