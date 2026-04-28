package com.cboard.owlswap.owlswap_backend.stripe.webhook;

import com.cboard.owlswap.owlswap_backend.stripe.config.StripeProperties;
import com.cboard.owlswap.owlswap_backend.stripe.webhook.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
//import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    private final StripeProperties stripeProperties;
    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeProperties stripeProperties,
                                   StripeWebhookService stripeWebhookService) {
        this.stripeProperties = stripeProperties;
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request,
                                                @RequestHeader("Stripe-Signature") String signature)
            throws IOException {

        /*String payload;
        try (BufferedReader reader = request.getReader()) {
            payload = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }*/

        String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        Event event;
        try {
            event = Webhook.constructEvent(
                    payload,
                    signature,
                    stripeProperties.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Stripe signature");
        }

        stripeWebhookService.handleEvent(event);

        return ResponseEntity.ok("Webhook received");
    }
}