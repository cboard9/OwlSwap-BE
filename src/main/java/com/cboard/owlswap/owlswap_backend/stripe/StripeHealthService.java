package com.cboard.owlswap.owlswap_backend.stripe;

import com.cboard.owlswap.owlswap_backend.stripe.StripeProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeHealthService {

    private final StripeProperties stripeProperties;

    public StripeHealthService(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    public Map<String, Object> getStripeConfigStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("secretKeyConfigured",
                stripeProperties.getSecretKey() != null && !stripeProperties.getSecretKey().isBlank());
        result.put("publishableKeyConfigured",
                stripeProperties.getPublishableKey() != null && !stripeProperties.getPublishableKey().isBlank());
        result.put("webhookSecretConfigured",
                stripeProperties.getWebhookSecret() != null && !stripeProperties.getWebhookSecret().isBlank());
        result.put("platformFeePercent", stripeProperties.getPlatformFeePercent());
        return result;
    }
}