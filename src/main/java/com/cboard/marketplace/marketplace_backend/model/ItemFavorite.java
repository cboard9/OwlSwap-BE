package com.cboard.marketplace.marketplace_backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "item_favorites")
public class ItemFavorite
{
    @EmbeddedId
    private ItemFavoriteId itemFavoriteId;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public ItemFavorite() {
    }

    public ItemFavorite(ItemFavoriteId itemFavoriteId, Item item, User user) {
        this.itemFavoriteId = itemFavoriteId;
        this.item = item;
        this.user = user;
    }

    public ItemFavoriteId getItemSubscriptionsId() {
        return itemFavoriteId;
    }

    public void setItemSubscriptionsId(ItemFavoriteId itemFavoriteId) {
        this.itemFavoriteId = itemFavoriteId;
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
