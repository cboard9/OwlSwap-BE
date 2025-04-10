package com.cboard.marketplace.marketplace_backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "user_subscriptions")
public class UserSubscriptions
{
    @EmbeddedId
    private UserSubscriptionsId userSubscriptionsId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("subscriberId")
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    public UserSubscriptions() {
    }

    public UserSubscriptions(UserSubscriptionsId userSubscriptionsId, User user, User subscriber) {
        this.userSubscriptionsId = userSubscriptionsId;
        this.user = user;
        this.subscriber = subscriber;
    }

    public UserSubscriptionsId getUserSubscriptionsId() {
        return userSubscriptionsId;
    }

    public void setUserSubscriptionsId(UserSubscriptionsId userSubscriptionsId) {
        this.userSubscriptionsId = userSubscriptionsId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(User subscriber) {
        this.subscriber = subscriber;
    }
}
