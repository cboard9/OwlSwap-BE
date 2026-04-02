package com.cboard.owlswap.owlswap_backend.stripe.checkout;

import com.cboard.owlswap.owlswap_backend.dao.OrderDao;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.exception.NotAvailableException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.User;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import com.cboard.owlswap.owlswap_backend.model.orders.OrderStatus;
import com.cboard.owlswap.owlswap_backend.model.orders.PaymentProvider;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class StripeCheckoutService {

    private final OrderDao orderDao;
    private final CurrentUser currentUser;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${stripe.platform.fee.percent}")
    private Double platformFeePercent;

    public StripeCheckoutService(OrderDao orderDao,
                                 CurrentUser currentUser) {
        this.orderDao = orderDao;
        this.currentUser = currentUser;
    }

    @Transactional
    public Session createCheckoutSession(Integer orderId) throws StripeException {
        Integer userId = currentUser.userId();

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        // Only the buyer can initiate checkout for this order
        if (!order.getBuyer().getUserId().equals(userId)) {
            //throw new ForbiddenException("You cannot pay for this order.");
            throw new AccessDeniedException("You cannot pay for this order.");
        }

        // Must still be pending
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be checked out.");
        }

        if (order.getCheckoutSessionId() != null && !order.getCheckoutSessionId().isBlank()) {
            throw new BadRequestException("A checkout session already exists for this order.");
        }

        // Reservation must not be expired
        if (order.getReservedUntil() != null && order.getReservedUntil().isBefore(LocalDateTime.now())) {
            throw new NotAvailableException("This reservation has expired.");
        }

        // Seller must have a Stripe-connected account ready
        if (order.getItem().getUser() == null) {
            throw new BadRequestException("Item has no seller.");
        }

        User seller = order.getItem().getUser();

        if (seller.getStripeAccountId() == null || seller.getStripeAccountId().isBlank()) {
            throw new BadRequestException("Seller has not connected Stripe.");
        }

        if (!seller.isStripeChargesEnabled() || !seller.isStripePayoutsEnabled()) {
            throw new BadRequestException("Seller is not ready to accept payments yet.");
        }

        // Prevent duplicate checkout sessions for orders already linked to a successful/active flow
        // For now, allow re-creation if desired; later you can decide whether to reuse an open session.
        long amountInCents = toSmallestCurrencyUnit(order.getAmount());
        long applicationFeeAmount = calculateApplicationFee(amountInCents, platformFeePercent);

        String successUrl = frontendBaseUrl + "/checkout/success?orderId=" + order.getOrderId();
        String cancelUrl = frontendBaseUrl + "/checkout/cancel?orderId=" + order.getOrderId()
                + "&session_id={CHECKOUT_SESSION_ID}";

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(order.getOrderId().toString())
                .putMetadata("order_id", order.getOrderId().toString())
                .putMetadata("buyer_id", order.getBuyer().getUserId().toString())
                .putMetadata("seller_id", order.getSeller().getUserId().toString())
                .putMetadata("item_id", order.getItem().getItemId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(order.getCurrency().toLowerCase())
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(order.getItem().getName())
                                                                .setDescription(order.getItem().getDescription())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .setApplicationFeeAmount(applicationFeeAmount)
                                .setTransferData(
                                        SessionCreateParams.PaymentIntentData.TransferData.builder()
                                                .setDestination(seller.getStripeAccountId())
                                                .build()
                                )
                                .putMetadata("order_id", order.getOrderId().toString())
                                .build()
                )
                .build();

        Session session = Session.create(params);

        // Persist Stripe references on the order
        order.setPaymentProvider(PaymentProvider.STRIPE);
        order.setCheckoutSessionId(session.getId());
        order.setLatestPaymentStatus(session.getPaymentStatus());
        orderDao.save(order);

        return session;
    }

    private long toSmallestCurrencyUnit(BigDecimal amount) {
        return amount
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private long calculateApplicationFee(long amountInCents, Double feePercent) {
        if (feePercent == null || feePercent <= 0) {
            return 0L;
        }

        BigDecimal fee = BigDecimal.valueOf(amountInCents)
                .multiply(BigDecimal.valueOf(feePercent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

        return fee.longValueExact();
    }
}