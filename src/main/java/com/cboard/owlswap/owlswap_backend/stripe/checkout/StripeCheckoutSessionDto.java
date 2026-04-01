package com.cboard.owlswap.owlswap_backend.stripe.checkout;

public class StripeCheckoutSessionDto {

    private String sessionId;
    private String url;

    public StripeCheckoutSessionDto() {
    }

    public StripeCheckoutSessionDto(String sessionId, String url) {
        this.sessionId = sessionId;
        this.url = url;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}