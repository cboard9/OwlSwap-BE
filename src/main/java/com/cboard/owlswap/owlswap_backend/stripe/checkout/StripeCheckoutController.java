package com.cboard.owlswap.owlswap_backend.stripe.checkout;

import com.cboard.owlswap.owlswap_backend.stripe.checkout.StripeCheckoutSessionDto;
import com.cboard.owlswap.owlswap_backend.stripe.checkout.StripeCheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe-orders")
public class StripeCheckoutController {

    private final StripeCheckoutService stripeCheckoutService;

    public StripeCheckoutController(StripeCheckoutService stripeCheckoutService) {
        this.stripeCheckoutService = stripeCheckoutService;
    }

    @PostMapping("/{id}/checkout-session")
    public ResponseEntity<StripeCheckoutSessionDto> createCheckoutSession(@PathVariable("id") Integer orderId)
            throws StripeException {

        Session session = stripeCheckoutService.createCheckoutSession(orderId);

        return ResponseEntity.ok(
                new StripeCheckoutSessionDto(session.getId(), session.getUrl())
        );
    }
}