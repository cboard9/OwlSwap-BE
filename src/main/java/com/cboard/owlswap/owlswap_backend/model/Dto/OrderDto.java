package com.cboard.owlswap.owlswap_backend.model.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDto {
    private Integer orderId;
    private Integer itemId;
    private Integer buyerId;
    private Integer sellerId;

    private BigDecimal amount;
    private String currency;

    private String status;
    private LocalDateTime reservedUntil;

    private LocalDateTime createdAt;
    private String refundId;
    private String refundReason;
    private LocalDateTime refundedAt;
    private String fulfillmentMethod;
    private LocalDateTime pickupCodeGeneratedAt;
    private LocalDateTime readyForPickupAt;
    private LocalDateTime fulfilledAt;
    private LocalDateTime refundRequestedAt;
    private String refundRequestReason;
    private LocalDateTime refundDecisionAt;
    private String refundDecisionReason;
    private String statusBeforeRefundRequest;


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public String getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(String fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
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

    public String getStatusBeforeRefundRequest() {
        return statusBeforeRefundRequest;
    }

    public void setStatusBeforeRefundRequest(String statusBeforeRefundRequest) {
        this.statusBeforeRefundRequest = statusBeforeRefundRequest;
    }
}

