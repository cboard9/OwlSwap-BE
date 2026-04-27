package com.cboard.owlswap.owlswap_backend.stripe.orders;

public enum OrderStatus {
    PENDING,     // reserved, not paid
    PAID,
    READY_FOR_PICKUP,
    FULFILLED,
    CANCELLED,
    EXPIRED,
    REFUNDED,
    REFUND_REQUESTED,
    REFUND_DENIED
}
