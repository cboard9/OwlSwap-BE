package com.cboard.owlswap.owlswap_backend.stripe;

public class StripeSellerStatusDto {

    private boolean hasStripeAccount;
    private String stripeAccountId;
    private boolean onboardingComplete;
    private boolean chargesEnabled;
    private boolean payoutsEnabled;

    public StripeSellerStatusDto(boolean hasStripeAccount,
                                 String stripeAccountId,
                                 boolean onboardingComplete,
                                 boolean chargesEnabled,
                                 boolean payoutsEnabled) {
        this.hasStripeAccount = hasStripeAccount;
        this.stripeAccountId = stripeAccountId;
        this.onboardingComplete = onboardingComplete;
        this.chargesEnabled = chargesEnabled;
        this.payoutsEnabled = payoutsEnabled;
    }

    public boolean isHasStripeAccount() {
        return hasStripeAccount;
    }

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public boolean isOnboardingComplete() {
        return onboardingComplete;
    }

    public boolean isChargesEnabled() {
        return chargesEnabled;
    }

    public boolean isPayoutsEnabled() {
        return payoutsEnabled;
    }
}