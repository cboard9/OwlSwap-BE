package com.cboard.owlswap.owlswap_backend.stripe.checkout;

import com.cboard.owlswap.owlswap_backend.dao.ItemDao;
import com.cboard.owlswap.owlswap_backend.dao.OrderDao;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Item;
import com.cboard.owlswap.owlswap_backend.stripe.orders.ListingStatus;
import com.cboard.owlswap.owlswap_backend.stripe.orders.Order;
import com.cboard.owlswap.owlswap_backend.stripe.orders.OrderStatus;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StripeRefundService {

    private final OrderDao orderDao;
    private final ItemDao itemDao;
    private final CurrentUser currentUser;

    public StripeRefundService(OrderDao orderDao,
                               ItemDao itemDao,
                               CurrentUser currentUser) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
        this.currentUser = currentUser;
    }

    @Transactional
    public Order refundOrder(Integer orderId, String refundReason) throws StripeException {
        if (!currentUser.isEmailVerified()) {
            throw new AccessDeniedException("Email verification required.");
        }

        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        // For now: seller can refund paid orders.
        // Later may also allow admin.
        if (!order.getSeller().getUserId().equals(userId) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You do not have permission to refund this order.");
        }

        if (order.getStatus() != OrderStatus.PAID
                && order.getStatus() != OrderStatus.READY_FOR_PICKUP
                && order.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new BadRequestException("Only paid, ready for pickup, or refund requested orders can be refunded.");
        }

        if (order.getPaymentIntentId() == null || order.getPaymentIntentId().isBlank()) {
            throw new BadRequestException("Order does not have a Stripe payment intent ID.");
        }

        if (order.getRefundId() != null && !order.getRefundId().isBlank()) {
            throw new BadRequestException("This order has already been refunded.");
        }

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(order.getPaymentIntentId())
                .setReverseTransfer(true)
                .setRefundApplicationFee(true)
                .putMetadata("order_id", order.getOrderId().toString())
                .putMetadata("item_id", order.getItem().getItemId().toString())
                .build();

        Refund refund = Refund.create(params);

        order.setRefundId(refund.getId());
        order.setRefundReason(refundReason);
        order.setRefundedAt(LocalDateTime.now());
        order.setLatestPaymentStatus(refund.getStatus());
        order.setStatus(OrderStatus.REFUNDED);
        orderDao.save(order);

        Item item = itemDao.findByIdForUpdate(order.getItem().getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found."));

        item.setListingStatus(ListingStatus.AVAILABLE);
        item.setReservedByOrder(null);
        item.setReservedUntil(null);
        item.setAvailable(true); // legacy sync
        itemDao.save(item);

        return order;
    }
}
