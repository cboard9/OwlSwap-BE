package com.cboard.owlswap.owlswap_backend.model.orders;

public enum OrderStatus {
    PENDING,     // reserved, not paid
    PAID,        // Stripe webhook sets this later
    FULFILLED,
    CANCELLED,
    EXPIRED,
    REFUNDED
}
