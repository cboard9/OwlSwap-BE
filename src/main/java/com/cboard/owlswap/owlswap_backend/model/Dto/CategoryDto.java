package com.cboard.owlswap.owlswap_backend.model.Dto;

public class CategoryDto {
    private int categoryId;
    private String name;

    public CategoryDto() {
    }

    public CategoryDto(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}