package com.cboard.owlswap.owlswap_backend.model.Dto;

import jakarta.validation.constraints.Size;

public class DecideRefundRequestDto {

    @Size(max = 255, message = "Decision reason must be 255 characters or less.")
    private String decisionReason;

    public DecideRefundRequestDto() {
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
}
