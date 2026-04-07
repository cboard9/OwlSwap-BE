package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.model.Dto.OrderDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.OrderToDtoMapper;
import com.cboard.owlswap.owlswap_backend.model.orders.CreateOrderRequest;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import com.cboard.owlswap.owlswap_backend.service.OrderService;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

 /*   @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Integer orderId) {
        return ResponseEntity.ok(mapper.toDto(orderService.cancelOrder(orderId)));
    }*/

/*    // TEMP for now (simulate payment)
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderDto> pay(@PathVariable Integer orderId) {
        return ResponseEntity.ok(mapper.toDto(orderService.markPaid(orderId)));
    }*/

    @PostMapping("/{orderId}/fulfill")
    public ResponseEntity<OrderDto> fulfill(@PathVariable Integer orderId) {
        return ResponseEntity.ok(mapper.toDto(orderService.fulfill(orderId)));
    }

    @GetMapping("/my-purchases")
    public ResponseEntity<List<OrderDto>> getMyPurchases()
    {
        return ResponseEntity.ok(orderService.getMyPurchases());
    }

    @GetMapping("/my-sales")
    public ResponseEntity<List<OrderDto>> getMySales()
    {
        return ResponseEntity.ok(orderService.getMySales());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderDto>> getAllOrdersByBuyer(@PathVariable("buyerId") int buyerId)
    {
        return ResponseEntity.ok(orderService.getAllOrdersByBuyer(buyerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderDto>> getAllOrdersBySeller(@PathVariable("sellerId") int sellerId)
    {
        return ResponseEntity.ok(orderService.getAllOrdersBySeller(sellerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("orderId") int orderId)
    {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getAllOrders()
    {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


}
