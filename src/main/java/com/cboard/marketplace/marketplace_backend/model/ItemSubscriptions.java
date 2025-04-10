package com.cboard.marketplace.marketplace_backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "item_subscriptions")
public class ItemSubscriptions
{
    @EmbeddedId
    private ItemSubscriptionsId itemSubscriptionsId;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public ItemSubscriptions() {
    }

    public ItemSubscriptions(ItemSubscriptionsId itemSubscriptionsId, Item item, User user) {
        this.itemSubscriptionsId = itemSubscriptionsId;
        this.item = item;
        this.user = user;
    }

    public ItemSubscriptionsId getItemSubscriptionsId() {
        return itemSubscriptionsId;
    }

    public void setItemSubscriptionsId(ItemSubscriptionsId itemSubscriptionsId) {
        this.itemSubscriptionsId = itemSubscriptionsId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
