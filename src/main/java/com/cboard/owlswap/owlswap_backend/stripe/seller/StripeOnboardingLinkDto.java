package com.cboard.owlswap.owlswap_backend.stripe.seller;

public class StripeOnboardingLinkDto {

    private String url;

    public StripeOnboardingLinkDto() {
    }

    public StripeOnboardingLinkDto(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}