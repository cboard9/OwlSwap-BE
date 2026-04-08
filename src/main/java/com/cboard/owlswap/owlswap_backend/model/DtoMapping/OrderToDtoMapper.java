package com.cboard.owlswap.owlswap_backend.model.DtoMapping;

import com.cboard.owlswap.owlswap_backend.model.Dto.OrderDto;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderToDtoMapper
{
    public OrderDto toDto(Order o) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(o.getOrderId());
        dto.setItemId(o.getItem().getItemId());
        dto.setBuyerId(o.getBuyer().getUserId());
        dto.setSellerId(o.getSeller().getUserId());
        dto.setAmount(o.getAmount());
        dto.setCurrency(o.getCurrency());
        dto.setStatus(o.getStatus().name());
        dto.setReservedUntil(o.getReservedUntil());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setRefundId(o.getRefundId());
        dto.setRefundReason(o.getRefundReason());
        dto.setRefundedAt(o.getRefundedAt());
        return dto;
    }

}
