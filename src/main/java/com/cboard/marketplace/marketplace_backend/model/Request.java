package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "request")
//@DiscriminatorValue("request")
public class Request extends Item
{
    @NotNull(message = "Deadline is required...")
    private String deadline;

    public Request() {
    }

/*    public Request(int itemId, String name, String description, Double price, User user, Category category, String releaseDate, boolean available, Location location, String itemType, String image_name, String image_type, byte[] image_date, String deadline)
    {
        super(itemId, name, description, price, user, category, releaseDate, available, location, itemType, image_name, image_type, image_date);
        this.deadline = deadline;
    }*/

    public Request(int itemId, String name, String description, Double price, User user, Category category, String releaseDate, boolean available, Location location, String itemType, List<ItemImage> images, String deadline)
    {
        super(itemId, name, description, price, user, category, releaseDate, available, location, itemType, images);
        this.deadline = deadline;
    }


    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}