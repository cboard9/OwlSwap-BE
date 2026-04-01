package com.cboard.owlswap.owlswap_backend.stripe;

import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.User;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.stripe.seller.StripeSellerStatusDto;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeConnectService {

    private final UserDao userDao;
    private final CurrentUser currentUser;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public StripeConnectService(UserDao userDao, CurrentUser currentUser) {
        this.userDao = userDao;
        this.currentUser = currentUser;
    }

    @Transactional
    public String createOrRefreshOnboardingLink() throws StripeException {
        Integer userId = currentUser.userId();

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        // Create connected account if user doesn't already have one
        if (user.getStripeAccountId() == null || user.getStripeAccountId().isBlank()) {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setEmail(user.getEmail())
                    .putMetadata("platform_user_id", String.valueOf(user.getUserId()))
                    .build();

            Account account = Account.create(params);

            user.setStripeAccountId(account.getId());
            user.setStripeOnboardingComplete(false);
            user.setStripeChargesEnabled(false);
            user.setStripePayoutsEnabled(false);
            userDao.save(user);
        }

        String refreshUrl = frontendBaseUrl + "/seller/stripe/reauth";
        String returnUrl = frontendBaseUrl + "/seller/stripe/return";

        AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
                .setAccount(user.getStripeAccountId())
                .setRefreshUrl(refreshUrl)
                .setReturnUrl(returnUrl)
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(linkParams);
        return accountLink.getUrl();
    }

    @Transactional
    public StripeSellerStatusDto refreshSellerStripeStatus() throws StripeException {
        Integer userId = currentUser.userId();

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        if (user.getStripeAccountId() == null || user.getStripeAccountId().isBlank()) {
            return new StripeSellerStatusDto(
                    false,
                    null,
                    false,
                    false,
                    false
            );
        }

        Account account = Account.retrieve(user.getStripeAccountId());

        boolean chargesEnabled = Boolean.TRUE.equals(account.getChargesEnabled());
        boolean payoutsEnabled = Boolean.TRUE.equals(account.getPayoutsEnabled());

        boolean onboardingComplete = chargesEnabled && payoutsEnabled;

        user.setStripeChargesEnabled(chargesEnabled);
        user.setStripePayoutsEnabled(payoutsEnabled);
        user.setStripeOnboardingComplete(onboardingComplete);
        userDao.save(user);

        return new StripeSellerStatusDto(
                true,
                user.getStripeAccountId(),
                onboardingComplete,
                chargesEnabled,
                payoutsEnabled
        );
    }
}