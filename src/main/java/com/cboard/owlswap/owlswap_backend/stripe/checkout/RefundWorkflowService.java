package com.cboard.owlswap.owlswap_backend.stripe.checkout;

import com.cboard.owlswap.owlswap_backend.dao.OrderDao;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.stripe.orders.Order;
import com.cboard.owlswap.owlswap_backend.stripe.orders.OrderStatus;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import com.cboard.owlswap.owlswap_backend.stripe.checkout.StripeRefundService;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefundWorkflowService {

    private final OrderDao orderDao;
    private final CurrentUser currentUser;
    private final StripeRefundService stripeRefundService;

    public RefundWorkflowService(OrderDao orderDao,
                                 CurrentUser currentUser,
                                 StripeRefundService stripeRefundService) {
        this.orderDao = orderDao;
        this.currentUser = currentUser;
        this.stripeRefundService = stripeRefundService;
    }

    @Transactional
    public Order requestRefund(Integer orderId, String reason) {
        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        if (!order.getBuyer().getUserId().equals(userId)) {
            throw new AccessDeniedException("Only the buyer can request a refund.");
        }

        if (order.getStatus() != OrderStatus.PAID
                && order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new BadRequestException("Refund can only be requested for paid or ready-for-pickup orders.");
        }

        if (order.getRefundId() != null && !order.getRefundId().isBlank()) {
            throw new BadRequestException("This order has already been refunded.");
        }

        order.setStatusBeforeRefundRequest(order.getStatus());
        order.setStatus(OrderStatus.REFUND_REQUESTED);
        order.setRefundRequestedAt(LocalDateTime.now());
        order.setRefundRequestReason(reason);
        order.setRefundDecisionAt(null);
        order.setRefundDecisionReason(null);

        return orderDao.save(order);
    }

    @Transactional
    public Order approveRefund(Integer orderId, String decisionReason) throws StripeException {
        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        if (!canApproveRefund(order, userId)) {
            throw new AccessDeniedException("You do not have permission to approve this refund.");
        }

        if (order.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new BadRequestException("Only refund-requested orders can be approved.");
        }

        order.setRefundDecisionAt(LocalDateTime.now());
        order.setRefundDecisionReason(decisionReason);

        /*
         * This calls your existing Stripe refund logic:
         * - validates paymentIntentId
         * - creates Stripe refund
         * - uses reverse_transfer=true
         * - uses refund_application_fee=true
         * - updates status to REFUNDED
         * - makes item AVAILABLE
         */
        return stripeRefundService.refundOrder(
                orderId,
                decisionReason != null && !decisionReason.isBlank()
                        ? decisionReason
                        : order.getRefundRequestReason()
        );
    }

    @Transactional
    public Order denyRefund(Integer orderId, String decisionReason) {
        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        if (!canApproveRefund(order, userId)) {
            throw new AccessDeniedException("You do not have permission to deny this refund.");
        }

        if (order.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new BadRequestException("Only refund-requested orders can be denied.");
        }

        OrderStatus previous = order.getStatusBeforeRefundRequest();

        order.setRefundDecisionAt(LocalDateTime.now());
        order.setRefundDecisionReason(decisionReason);
        order.setStatus(OrderStatus.REFUND_DENIED);

        /*
         * Option A: Leave REFUND_DENIED as final visible status.
         * Option B: Return it to previous after recording decision.
         *
         * I recommend Option B for smoother fulfillment flow.
         */
        if (previous == OrderStatus.PAID || previous == OrderStatus.READY_FOR_PICKUP) {
            order.setStatus(previous);
        }

        order.setStatusBeforeRefundRequest(null);

        return orderDao.save(order);
    }

    private boolean canApproveRefund(Order order, Integer userId) {
        // Seller can approve refund for their own sale.
        if(order.getSeller() != null && order.getSeller().getUserId().equals(userId)) {
            return true;
        }

         return currentUser.hasRole("ROLE_ADMIN");

    }
}
