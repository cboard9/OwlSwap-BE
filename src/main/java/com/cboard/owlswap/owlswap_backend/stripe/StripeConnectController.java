package com.cboard.owlswap.owlswap_backend.stripe;

import com.cboard.owlswap.owlswap_backend.stripe.StripeOnboardingLinkDto;
import com.cboard.owlswap.owlswap_backend.stripe.StripeConnectService;
import com.cboard.owlswap.owlswap_backend.stripe.StripeSellerStatusDto;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/connect")
public class StripeConnectController {

    private final StripeConnectService stripeConnectService;

    public StripeConnectController(StripeConnectService stripeConnectService) {
        this.stripeConnectService = stripeConnectService;
    }

    @PostMapping("/onboarding-link")
    public ResponseEntity<StripeOnboardingLinkDto> createOnboardingLink() throws StripeException
    {
        String url = stripeConnectService.createOrRefreshOnboardingLink();
        return ResponseEntity.ok(new StripeOnboardingLinkDto(url));
    }

    @GetMapping("/status")
    public ResponseEntity<StripeSellerStatusDto> getStatus() throws StripeException {
        return ResponseEntity.ok(stripeConnectService.refreshSellerStripeStatus());
    }
}