package com.cboard.owlswap.owlswap_backend.model.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateSellerLocationRequest {

    @NotBlank(message = "Location name is required.")
    @Size(max = 80, message = "Location name must be 80 characters or less.")
    private String name;

    @NotBlank(message = "Address line 1 is required.")
    @Size(max = 120, message = "Address line 1 must be 120 characters or less.")
    private String addressLine1;

    @Size(max = 120, message = "Address line 2 must be 120 characters or less.")
    private String addressLine2;

    @NotBlank(message = "City is required.")
    @Size(max = 80, message = "City must be 80 characters or less.")
    private String city;

    @NotBlank(message = "State is required.")
    @Size(max = 40, message = "State must be 40 characters or less.")
    private String state;

    @NotBlank(message = "Postal code is required.")
    @Size(max = 20, message = "Postal code must be 20 characters or less.")
    private String postalCode;

    private String country = "US";

    private Double latitude;
    private Double longitude;

    public UpdateSellerLocationRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}