package com.cboard.owlswap.owlswap_backend.stripe.orders;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(@NotNull Integer itemId) {
}
