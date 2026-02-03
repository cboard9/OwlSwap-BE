package com.cboard.marketplace.marketplace_backend.model.Dto.ItemMetadata;

import java.util.List;

public class ItemTypeSchema {
    private String itemType;
    private List<FieldSchema> fields;

    public ItemTypeSchema() {
    }
    public ItemTypeSchema(String itemType, List<FieldSchema> fields) {
        this.itemType = itemType;
        this.fields = fields;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public List<FieldSchema> getFields() {
        return fields;
    }

    public void setFields(List<FieldSchema> fields) {
        this.fields = fields;
    }
}
