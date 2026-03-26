package com.cboard.owlswap.owlswap_backend.stripe;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {

    private String secretKey;
    private String publishableKey;
    private String webhookSecret;
    private Double platformFeePercent;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public Double getPlatformFeePercent() {
        return platformFeePercent;
    }

    public void setPlatformFeePercent(Double platformFeePercent) {
        this.platformFeePercent = platformFeePercent;
    }
}