package com.cboard.owlswap.owlswap_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item_image")
public class ItemImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    private String image_name;
    private String image_type;
    private byte[] image_date;

    public ItemImage() {
    }

    public ItemImage(int imageId, Item item, String image_name, String image_type, byte[] image_date) {
        this.imageId = imageId;
        this.item = item;
        this.image_name = image_name;
        this.image_type = image_type;
        this.image_date = image_date;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public byte[] getImage_date() {
        return image_date;
    }

    public void setImage_date(byte[] image_date) {
        this.image_date = image_date;
    }
}
