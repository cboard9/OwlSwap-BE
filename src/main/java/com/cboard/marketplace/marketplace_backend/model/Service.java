package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;


@Entity
@Table(name = "service")
//@DiscriminatorValue("service")
public class Service extends Item
{
    private int durationMinutes;

    public Service() {
    }

    public Service(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

}