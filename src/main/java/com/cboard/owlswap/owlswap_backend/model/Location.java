package com.cboard.owlswap.owlswap_backend.model;

import com.cboard.owlswap.owlswap_backend.model.Dto.LocationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationId;

    @Column(nullable = false)
    @NotBlank(message = "Location name is required.")
    private String name;

    // Keep old address column temporarily for migration compatibility
    private String address;

    @Size(max=120, message = "Address line 1 must be 120 characters or less.")
    @Column(name = "address_line_1")
    private String addressLine1;

    @Size(max=120, message = "Address line 1 must be 120 characters or less.")
    @Column(name = "address_line_2")
    private String addressLine2;

    @Size(max=80, message = "Address line 1 must be 120 characters or less.")
    private String city;

    @Size(max=40, message = "Address line 1 must be 120 characters or less.")
    private String state;

    @Size(max=20, message = "Address line 1 must be 120 characters or less.")
    @Column(name = "postal_code")
    private String postalCode;

    @Column(nullable = false)
    private String country = "US";

    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType = LocationType.PRESET_MEETUP;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private boolean preset = false;

    @Column(nullable = false)
    private boolean active = true;

    public Location() {
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // temporary legacy field
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isPreset() {
        return preset;
    }

    public void setPreset(boolean preset) {
        this.preset = preset;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
