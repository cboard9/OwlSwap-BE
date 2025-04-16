package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "product")
//@DiscriminatorValue("product")
public class Product extends Item
{
    @NotNull(message = "Quantity is required...")
    private Integer quantity;
    private String brand;

    public Product() {
    }

    public Product(int itemId, String name, String description, Double price, Category category, String releaseDate, boolean available, Location location, String itemType, String image_name, String image_type, byte[] image_date, Integer quantity, String brand)
    {
        super(itemId, name, description, price, category, releaseDate, available, location, itemType, image_name, image_type, image_date);
        this.quantity = quantity;
        this.brand = brand;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
