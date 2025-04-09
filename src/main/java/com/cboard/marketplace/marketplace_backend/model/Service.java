package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@DiscriminatorValue("service")
public class Service
{
    @Id
    private int serviceId;
    private int durationMinutes;

    public Service() {
    }

    public Service(int serviceId, int durationMinutes) {
        this.serviceId = serviceId;
        this.durationMinutes = durationMinutes;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

}