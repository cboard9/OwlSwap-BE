package com.cboard.owlswap.owlswap_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "service_area_zip")
public class ServiceAreaZip {

    @Id
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 100)
    private String label;

    public ServiceAreaZip() {
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
