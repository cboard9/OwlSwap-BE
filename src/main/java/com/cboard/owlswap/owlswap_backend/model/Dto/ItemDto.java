package com.cboard.owlswap.owlswap_backend.model.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "itemType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductDto.class, name = "product"),
        @JsonSubTypes.Type(value = ServiceDto.class, name = "service"),
        @JsonSubTypes.Type(value = RequestDto.class, name = "request")
})


public abstract class ItemDto
{
    private int itemId;
    @NotNull(message = "Name is required...")
    @NotBlank(message = "Name must not be blank")
    private String name;
    private String description;
    @NotNull(message = "Price is required...")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be ≥ 0")
    private Double price;
    private int userId;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Release date is required")
    @Pattern(
            regexp = "\\d{4}-\\d{2}-\\d{2}",
            message = "Release date must be YYYY-MM-DD"
    )
    private String releaseDate;
    private boolean available;

    private String location;
    @NotNull(message = "Location is required")
    private Integer locationId;
    @NotBlank(message = "Item type is required")
    private String itemType;
    private List<ItemImageDto> images = new ArrayList<>();
    /*private String image_name;
    private String image_type;
    private byte[] image_date;*/
    private Map<@NotBlank String, @NotNull(message="Value cannot be blank") Object> specificFields;



    public ItemDto() {
    }

/*
    public ItemDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String itemType, String image_name, String image_type, byte[] image_date) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
        this.category = category;
        this.releaseDate = releaseDate;
        this.available = available;
        //this.location = location;
        this.itemType = itemType;
        this.image_name = image_name;
        this.image_type = image_type;
        this.image_date = image_date;
    }
*/

/*    public ItemDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String location, Integer locationId, String itemType, String image_name, String image_type, byte[] image_date) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
        this.category = category;
        this.releaseDate = releaseDate;
        this.available = available;
        this.location = location;
        this.locationId = locationId;
        this.itemType = itemType;
        this.image_name = image_name;
        this.image_type = image_type;
        this.image_date = image_date;
    }*/

    public ItemDto(int itemId, String name, String description, Double price, int userId, String category, String releaseDate, boolean available, String location, Integer locationId, String itemType, List<ItemImageDto> images) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
        this.category = category;
        this.releaseDate = releaseDate;
        this.available = available;
        this.location = location;
        this.locationId = locationId;
        this.itemType = itemType;
        this.images = images;
    }

    public Map<String, Serializable> getSpecificFields() {
        return Collections.emptyMap(); // base class default
    }

    public void setSpecificFields(Map<String, String> fields)
    {
        throw new UnsupportedOperationException("setSpecificFields not implemented for base ItemDto");

    }

    @JsonIgnore
    public String getSimpleName()
    {
        return "";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    //@JsonTypeId //prevents duplication when fetching from database ?
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

/*    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public byte[] getImage_date() {
        return image_date;
    }

    public void setImage_date(byte[] image_date) {
        this.image_date = image_date;
    }*/

    public List<ItemImageDto> getImages() {
        return images;
    }

    public void setImages(List<ItemImageDto> images) {
        this.images = images;
    }
}
