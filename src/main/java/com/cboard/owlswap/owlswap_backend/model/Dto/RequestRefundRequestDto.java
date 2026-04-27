package com.cboard.owlswap.owlswap_backend.model.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RequestRefundRequestDto {

    @NotBlank(message = "Refund request reason is required.")
    @Size(max = 255, message = "Refund request reason must be 255 characters or less.")
    private String reason;

    public RequestRefundRequestDto() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
