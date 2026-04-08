package com.cboard.owlswap.owlswap_backend.stripe.checkout;

import com.cboard.owlswap.owlswap_backend.model.Dto.OrderDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.RefundOrderRequestDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.OrderToDtoMapper;
import com.cboard.owlswap.owlswap_backend.stripe.orders.CreateOrderRequest;
import com.cboard.owlswap.owlswap_backend.stripe.orders.Order;
import com.cboard.owlswap.owlswap_backend.service.OrderService;
import com.cboard.owlswap.owlswap_backend.stripe.orders.pickup.ConfirmPickupRequest;
import com.cboard.owlswap.owlswap_backend.stripe.orders.pickup.PickupCodeResponseDto;
import com.cboard.owlswap.owlswap_backend.stripe.orders.pickup.PickupService;
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
    private StripeRefundService stripeRefundService;
    private final PickupService pickupService;
    private final OrderService orderService;
    private final OrderToDtoMapper orderToDtoMapper;

    public StripeCheckoutController(StripeCheckoutService stripeCheckoutService,
                                    StripeRefundService stripeRefundService,
                                    PickupService pickupService,
                                    OrderService orderService,
                                    OrderToDtoMapper orderToDtoMapper) {
        this.stripeCheckoutService = stripeCheckoutService;
        this.stripeRefundService = stripeRefundService;
        this.pickupService = pickupService;
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


/*    @PostMapping("/{orderId}/fulfill")
    public ResponseEntity<OrderDto> fulfill(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderToDtoMapper.toDto(orderService.fulfill(orderId)));
    }*/

    @PostMapping("/{id}/refund")
    public ResponseEntity<OrderDto> refundOrder(@PathVariable("id") Integer orderId,
                                                @Valid @RequestBody RefundOrderRequestDto request)
            throws StripeException {

        Order refunded = stripeRefundService.refundOrder(orderId, request.getReason());
        return ResponseEntity.ok(orderToDtoMapper.toDto(refunded));
    }

    @PostMapping("/{id}/pickup-code")
    public ResponseEntity<PickupCodeResponseDto> generatePickupCode(@PathVariable("id") Integer orderId) {
        return ResponseEntity.ok(pickupService.generatePickupCode(orderId));
    }

    @PostMapping("/{id}/ready-for-pickup")
    public ResponseEntity<OrderDto> markReadyForPickup(@PathVariable("id") Integer orderId) {
        Order order = pickupService.markReadyForPickup(orderId);
        return ResponseEntity.ok(orderToDtoMapper.toDto(order));
    }

    @PostMapping("/{id}/confirm-pickup")
    public ResponseEntity<OrderDto> confirmPickup(@PathVariable("id") Integer orderId,
                                                  @Valid @RequestBody ConfirmPickupRequest request) {
        Order order = pickupService.confirmPickup(orderId, request.getPickupCode());
        return ResponseEntity.ok(orderToDtoMapper.toDto(order));
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