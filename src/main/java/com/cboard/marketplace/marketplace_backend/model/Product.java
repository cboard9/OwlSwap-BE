package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@DiscriminatorValue("product")
public class Product
{
    @Id
    private int productId;
    private int quantity;
    private String brand;

    public Product() {
    }

    public Product(int productId, int quantity, String brand) {
        this.productId = productId;
        this.quantity = quantity;
        this.brand = brand;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
