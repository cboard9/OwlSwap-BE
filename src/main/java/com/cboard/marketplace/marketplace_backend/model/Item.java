package com.cboard.marketplace.marketplace_backend.model;

import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;


//entity maps this saying this is a table

@Entity
@Table(name = "item")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;
    private String name;
    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    private String releaseDate;
    private boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Location location;
    private String itemType;
    private String image_name;
    private String image_type;
    private byte[] image_date;


    public Item() {
    }

    public Item(int itemId, String name, double price, Category category, String releaseDate, boolean available, Location location, String itemType, String image_name, String image_type, byte[] image_date) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.releaseDate = releaseDate;
        this.available = available;
        this.location = location;
        this.itemType = itemType;
        this.image_name = image_name;
        this.image_type = image_type;
        this.image_date = image_date;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
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
