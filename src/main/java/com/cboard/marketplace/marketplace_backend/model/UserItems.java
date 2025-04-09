package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "user_items")
public class UserItems
{
    @EmbeddedId
    private UserItemsId userItemsId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    public UserItems() {
    }

    public UserItems(UserItemsId userItemsId, User user, Item item) {
        this.userItemsId = userItemsId;
        this.user = user;
        this.item = item;
    }

    public UserItemsId getUserItemsId() {
        return userItemsId;
    }

    public void setUserItemsId(UserItemsId userItemsId) {
        this.userItemsId = userItemsId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
