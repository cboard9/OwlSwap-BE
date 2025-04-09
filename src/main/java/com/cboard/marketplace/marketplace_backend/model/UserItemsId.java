package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class UserItemsId implements Serializable
{
    @Column(name = "user_id")
    private int userId;

    @Column(name = "item_id")
    private int itemId;

    public UserItemsId() {
    }

    public UserItemsId(int userId, int itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }


}

