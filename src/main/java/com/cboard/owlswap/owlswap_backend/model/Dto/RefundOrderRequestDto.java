package com.cboard.owlswap.owlswap_backend.model.Dto;

import jakarta.validation.constraints.NotBlank;

public class RefundOrderRequestDto {
    @NotBlank(message = "Refund reason is required.")
    private String reason;

    public RefundOrderRequestDto() {
    }

    public RefundOrderRequestDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
