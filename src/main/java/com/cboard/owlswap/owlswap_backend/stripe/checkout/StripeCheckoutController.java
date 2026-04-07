package com.cboard.owlswap.owlswap_backend.stripe.checkout;

import com.cboard.owlswap.owlswap_backend.model.Dto.OrderDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.OrderToDtoMapper;
import com.cboard.owlswap.owlswap_backend.model.orders.CreateOrderRequest;
import com.cboard.owlswap.owlswap_backend.model.orders.Order;
import com.cboard.owlswap.owlswap_backend.service.OrderService;
import com.cboard.owlswap.owlswap_backend.stripe.checkout.StripeCheckoutSessionDto;
import com.cboard.owlswap.owlswap_backend.stripe.checkout.StripeCheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class StripeCheckoutController {

    private final StripeCheckoutService stripeCheckoutService;
    private final OrderService orderService;
    private final OrderToDtoMapper orderToDtoMapper;

    public StripeCheckoutController(StripeCheckoutService stripeCheckoutService,
                                    OrderService orderService,
                                    OrderToDtoMapper orderToDtoMapper) {
        this.stripeCheckoutService = stripeCheckoutService;
        this.orderService = orderService;
        this.orderToDtoMapper = orderToDtoMapper;
    }

    @PostMapping("/{id}/checkout-session")
    public ResponseEntity<StripeCheckoutSessionDto> createCheckoutSession(@PathVariable("id") Integer orderId)
            throws StripeException {

        Session session = stripeCheckoutService.createCheckoutSession(orderId);

        return ResponseEntity.ok(
                new StripeCheckoutSessionDto(session.getId(), session.getUrl())
        );
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDto> create(@RequestBody @Valid CreateOrderRequest req) {
        Order order = orderService.createOrderAndReserveItem(req.itemId());
        return ResponseEntity.ok(orderToDtoMapper.toDto(order));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderToDtoMapper.toDto(orderService.cancelOrder(orderId)));
    }


    @PostMapping("/{orderId}/fulfill")
    public ResponseEntity<OrderDto> fulfill(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderToDtoMapper.toDto(orderService.fulfill(orderId)));
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