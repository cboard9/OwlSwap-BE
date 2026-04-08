package com.cboard.owlswap.owlswap_backend.stripe.orders.pickup;

import jakarta.validation.constraints.NotBlank;

public class ConfirmPickupRequest {

    @NotBlank(message = "Pickup code is required.")
    private String pickupCode;

    public ConfirmPickupRequest() {
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }
}
