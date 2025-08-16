package com.cboard.marketplace.marketplace_backend.model.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductDto extends ItemDto
{
    @NotNull(message = "Quantity is required...")
    @Min(value = 0, message = "Quantity cannot be less than 0...")
    private Integer quantity = 0;
    private String brand = "";

    public ProductDto() {
    }

    public ProductDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String location, Integer locationId, String itemType, String image_name, String image_type, byte[] image_date, Integer quantity, String brand)
    {
        super(itemId, name, description, price, userId, category, releaseDate, available, location, locationId, itemType, image_name, image_type, image_date);
        this.quantity = quantity;
        this.brand = brand;
    }

    @Override
    public String getSimpleName()
    {
        return "product";
    }

    @Override
    public Map<String, Serializable> getSpecificFields() {
        Map<String, Serializable> fields = new LinkedHashMap<>();
        fields.put("Brand", brand);
        fields.put("Quantity", quantity);
        return fields;
    }

    @Override
    public void setSpecificFields(Map<String, String> fields)
    {
        if (fields.containsKey("Brand")) {
            this.brand = fields.get("Brand");
        }
        if (fields.containsKey("Quantity")) {
            try {
                this.quantity = Integer.parseInt(fields.get("Quantity"));
            } catch (NumberFormatException e) {
                this.quantity = 0; // or fallback/default
            }
        }
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
