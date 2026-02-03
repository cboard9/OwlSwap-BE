package com.cboard.marketplace.marketplace_backend.model;
import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class ItemFavoriteId implements Serializable
{
    @Column(name = "item_id")
    private int itemId;
    @Column(name = "user_id")
    private int userId;

    public ItemFavoriteId() {
    }

    public ItemFavoriteId(int itemId, int userId) {
        this.itemId = itemId;
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
