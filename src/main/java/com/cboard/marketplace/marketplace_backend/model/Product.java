package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;


@Entity
@Table(name = "product")
//@DiscriminatorValue("product")
public class Product extends Item
{
    private int quantity;
    private String brand;

    public Product() {
    }

    public Product(int quantity, String brand) {
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
