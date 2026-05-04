package com.cboard.owlswap.owlswap_backend.stripe.orders;

import com.cboard.owlswap.owlswap_backend.model.Item;
import com.cboard.owlswap.owlswap_backend.model.UserArchive;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false)
    private UserArchive buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private UserArchive seller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;

    // Stripe placeholders
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_provider")
    private PaymentProvider paymentProvider;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "checkout_session_id")
    private String checkoutSessionId;

    @Column(name = "latest_payment_status")
    private String latestPaymentStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    @Column(name = "refund_id")
    private String refundId;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_method", nullable = false)
    private FulfillmentMethod fulfillmentMethod = FulfillmentMethod.PICKUP;

    @Column(name = "pickup_code_hash")
    private String pickupCodeHash;

    @Column(name = "pickup_code_generated_at")
    private LocalDateTime pickupCodeGeneratedAt;

    @Column(name = "ready_for_pickup_at")
    private LocalDateTime readyForPickupAt;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    //refund fields
    @Column(name = "refund_requested_at")
    private LocalDateTime refundRequestedAt;

    @Column(name = "refund_request_reason")
    private String refundRequestReason;

    @Column(name = "refund_decision_at")
    private LocalDateTime refundDecisionAt;

    @Column(name = "refund_decision_reason")
    private String refundDecisionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_before_refund_request")
    private OrderStatus statusBeforeRefundRequest;

    public Order() {}

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public UserArchive getBuyer() {
        return buyer;
    }

    public void setBuyer(UserArchive buyer) {
        this.buyer = buyer;
    }

    public UserArchive getSeller() {
        return seller;
    }

    public void setSeller(UserArchive seller) {
        this.seller = seller;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getCheckoutSessionId() {
        return checkoutSessionId;
    }

    public void setCheckoutSessionId(String checkoutSessionId) {
        this.checkoutSessionId = checkoutSessionId;
    }

    public String getLatestPaymentStatus() {
        return latestPaymentStatus;
    }

    public void setLatestPaymentStatus(String latestPaymentStatus) {
        this.latestPaymentStatus = latestPaymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public FulfillmentMethod getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(FulfillmentMethod fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public String getPickupCodeHash() {
        return pickupCodeHash;
    }

    public void setPickupCodeHash(String pickupCodeHash) {
        this.pickupCodeHash = pickupCodeHash;
    }

    public LocalDateTime getPickupCodeGeneratedAt() {
        return pickupCodeGeneratedAt;
    }

    public void setPickupCodeGeneratedAt(LocalDateTime pickupCodeGeneratedAt) {
        this.pickupCodeGeneratedAt = pickupCodeGeneratedAt;
    }

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public void setReadyForPickupAt(LocalDateTime readyForPickupAt) {
        this.readyForPickupAt = readyForPickupAt;
    }

    public LocalDateTime getFulfilledAt() {
        return fulfilledAt;
    }

    public void setFulfilledAt(LocalDateTime fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }

    public LocalDateTime getRefundRequestedAt() {
        return refundRequestedAt;
    }

    public void setRefundRequestedAt(LocalDateTime refundRequestedAt) {
        this.refundRequestedAt = refundRequestedAt;
    }

    public String getRefundRequestReason() {
        return refundRequestReason;
    }

    public void setRefundRequestReason(String refundRequestReason) {
        this.refundRequestReason = refundRequestReason;
    }

    public LocalDateTime getRefundDecisionAt() {
        return refundDecisionAt;
    }

    public void setRefundDecisionAt(LocalDateTime refundDecisionAt) {
        this.refundDecisionAt = refundDecisionAt;
    }

    public String getRefundDecisionReason() {
        return refundDecisionReason;
    }

    public void setRefundDecisionReason(String refundDecisionReason) {
        this.refundDecisionReason = refundDecisionReason;
    }

    public OrderStatus getStatusBeforeRefundRequest() {
        return statusBeforeRefundRequest;
    }

    public void setStatusBeforeRefundRequest(OrderStatus statusBeforeRefundRequest) {
        this.statusBeforeRefundRequest = statusBeforeRefundRequest;
    }
}
