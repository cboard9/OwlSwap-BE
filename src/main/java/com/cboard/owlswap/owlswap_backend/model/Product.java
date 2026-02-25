package com.cboard.owlswap.owlswap_backend.model;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;


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


    public Product(int itemId, String name, String description, Double price, User user, Category category, String releaseDate, boolean available, Location location, String itemType, List<ItemImage> images, Integer quantity, String brand)
    {
        super(itemId, name, description, price, user, category, releaseDate, available, location, itemType, images);
        this.quantity = quantity;
        this.brand = brand;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
