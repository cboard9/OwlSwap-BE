package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.model.Dto.OrderDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.OrderToDtoMapper;
import com.cboard.owlswap.owlswap_backend.model.orders.CreateOrderRequest;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import com.cboard.owlswap.owlswap_backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController
{
    private final OrderService orderService;
    private final OrderToDtoMapper mapper;

    public OrderController(OrderService orderService, OrderToDtoMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDto> create(@RequestBody @Valid CreateOrderRequest req) {
        Order order = orderService.createOrderAndReserveItem(req.itemId());
        return ResponseEntity.ok(mapper.toDto(order));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Integer orderId) {
        return ResponseEntity.ok(mapper.toDto(orderService.cancelOrder(orderId)));
    }

    // TEMP for now (simulate payment)
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderDto> pay(@PathVariable Integer orderId) {
        return ResponseEntity.ok(mapper.toDto(orderService.markPaid(orderId)));
    }

    @PostMapping("/{orderId}/fulfill")
    public ResponseEntity<OrderDto> fulfill(@PathVariable Integer orderId) {
        return ResponseEntity.ok(mapper.toDto(orderService.fulfill(orderId)));
    }


}
