package com.cboard.owlswap.owlswap_backend.model;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "request")
public class Request extends Item
{
    @NotNull(message = "Deadline is required...")
    private String deadline;

    public Request() {
    }

    public Request(Integer itemId, String name, String description, Double price, User user, Category category, String releaseDate, boolean available, Location location, String itemType, List<ItemImage> images, String deadline)
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