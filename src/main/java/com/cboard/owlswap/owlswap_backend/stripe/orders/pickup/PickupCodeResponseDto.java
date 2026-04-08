package com.cboard.owlswap.owlswap_backend.stripe.orders.pickup;

public class PickupCodeResponseDto {

    private Integer orderId;
    private String pickupCode;

    public PickupCodeResponseDto() {
    }

    public PickupCodeResponseDto(Integer orderId, String pickupCode) {
        this.orderId = orderId;
        this.pickupCode = pickupCode;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }
}
