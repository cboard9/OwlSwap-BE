package com.cboard.marketplace.marketplace_backend.model.Dto;

public class ItemImageDto {
    private int imageId;
    private int itemId;
    private String image_name;
    private String image_type;
    private byte[] image_date;

    public ItemImageDto() {
    }

    public ItemImageDto(int imageId, int itemId, String image_name, String image_type, byte[] image_date) {
        this.imageId = imageId;
        this.itemId = itemId;
        this.image_name = image_name;
        this.image_type = image_type;
        this.image_date = image_date;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getImage_name() {
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
    }
}
