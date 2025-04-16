package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "service")
//@DiscriminatorValue("service")
public class Service extends Item
{
    @NotNull(message = "Duration is required...")
    private Integer durationMinutes;

    public Service() {
    }

    public Service(int itemId, String name, String description, Double price, User user, Category category, String releaseDate, boolean available, Location location, String itemType, String image_name, String image_type, byte[] image_date, Integer durationMinutes)
    {
        super(itemId, name, description, price, user, category, releaseDate, available, location, itemType, image_name, image_type, image_date);
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

}