package com.cboard.owlswap.owlswap_backend.model.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceDto extends ItemDto
{
    @NotNull(message = "Duration is required...")
    @Min(value = 0, message = "Duration cannot be less than 0...")
    private Integer durationMinutes = 0;

    public ServiceDto() {
    }

/*    public ServiceDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String location, Integer locationId, String itemType, String image_name, String image_type, byte[] image_date, Integer durationMinutes)
    {
        super(itemId, name, description, price, userId, category, releaseDate, available, location, locationId, itemType, image_name, image_type, image_date);
        this.durationMinutes = durationMinutes;
    }*/

    public ServiceDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String location, Integer locationId, String itemType, List<ItemImageDto> images, Integer durationMinutes)
    {
        super(itemId, name, description, price, userId, category, releaseDate, available, location, locationId, itemType, images);
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String getSimpleName()
    {
        return "service";
    }

    @Override
    public Map<String, Serializable> getSpecificFields() {
        Map<String, Serializable> fields = new LinkedHashMap<>();
        fields.put("Duration [minutes]", durationMinutes);
        return fields;
    }

    @Override
    public void setSpecificFields(Map<String, String> fields)
    {
        if (fields.containsKey("Duration [minutes]")) {
            try {
                this.durationMinutes = Integer.parseInt(fields.get("Duration [minutes]"));
            } catch (NumberFormatException e) {
                this.durationMinutes = 0;
            }
        }
    }

    public Integer getDurationMinutes()
    {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes)
    {
        this.durationMinutes = durationMinutes;
    }
}
