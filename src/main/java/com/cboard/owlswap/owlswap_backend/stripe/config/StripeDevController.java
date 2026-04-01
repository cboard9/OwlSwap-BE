package com.cboard.owlswap.owlswap_backend.stripe.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StripeDevController {

    private final StripeHealthService stripeHealthService;

    public StripeDevController(StripeHealthService stripeHealthService) {
        this.stripeHealthService = stripeHealthService;
    }

    @GetMapping("/dev/stripe/status")
    public ResponseEntity<Map<String, Object>> stripeStatus() {
        return ResponseEntity.ok(stripeHealthService.getStripeConfigStatus());
    }
}