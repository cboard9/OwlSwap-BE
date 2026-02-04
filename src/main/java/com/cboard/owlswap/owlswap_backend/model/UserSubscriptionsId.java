package com.cboard.owlswap.owlswap_backend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class UserSubscriptionsId implements Serializable
{
    @Column(name = "user_id")
    private int userId;
    @Column(name = "subscriber_id")
    private int subscriberId;

    public UserSubscriptionsId() {
    }

    public UserSubscriptionsId(int userId, int subscriberId) {
        this.userId = userId;
        this.subscriberId = subscriberId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }
}
