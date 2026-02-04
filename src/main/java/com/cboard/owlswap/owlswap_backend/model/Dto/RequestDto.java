package com.cboard.owlswap.owlswap_backend.model.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RequestDto extends ItemDto
{


    @Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "Deadline must be YYYY/MM/DD")

    @NotNull(message = "Deadline is required...")
    private String deadline = "";

    public RequestDto() {
    }



/*    public RequestDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String location, Integer locationId, String itemType, String image_name, String image_type, byte[] image_date, String deadline)
    {
        super(itemId, name, description, price, userId, category, releaseDate, available, location, locationId, itemType, image_name, image_type, image_date);
        this.deadline = deadline;
    }*/

    public RequestDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String location, Integer locationId, String itemType, List<ItemImageDto> images, String deadline)
    {
        super(itemId, name, description, price, userId, category, releaseDate, available, location, locationId, itemType, images);
        this.deadline = deadline;
    }


    @Override
    public String getSimpleName()
    {
        return "request";
    }


    @Override
    public Map<String, Serializable> getSpecificFields() {
        Map<String, Serializable> fields = new LinkedHashMap<>();
        fields.put("Deadline", deadline);
        return fields;
    }

    @Override
    public void setSpecificFields(Map<String, String> fields)
    {
        if (fields.containsKey("Deadline"))
        {
            this.deadline = fields.get("Deadline");
        }
    }


    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
