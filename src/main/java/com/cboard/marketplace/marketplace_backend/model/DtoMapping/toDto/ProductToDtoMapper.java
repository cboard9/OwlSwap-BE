package com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto;


import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.model.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductToDtoMapper implements ItemToDtoMapper<Product>
{
    @Autowired
    ImageToDtoMapper imageMapper;
    public ProductToDtoMapper() {
    }

    @Override
    public ItemDto mapToDto(Product p) {
        ProductDto dto = new ProductDto(
                p.getItemId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                (p.getUser() != null) ? p.getUser().getUserId() : -1,
                (p.getCategory() != null) ? p.getCategory().getName() : null,
                p.getReleaseDate(),
                p.isAvailable(),
                (p.getLocation() != null) ? p.getLocation().getName() : null,
                (p.getLocation() != null) ? p.getLocation().getLocationId() : null,
                p.getItemType(),
                new ArrayList<>(),
                /*p.getImage_name(),
                p.getImage_type(),
                p.getImage_date(),*/
                p.getQuantity(),
                p.getBrand()
        );

        List<ItemImageDto> images = new ArrayList<>();
        for(ItemImage img : p.getImages())
            images.add(imageMapper.mapToDto(img));
        dto.setImages(images);

        if (p.getLocation() != null) {
            dto.setLocationId(p.getLocation().getLocationId());
        }
        return dto;
    }

    @Override
    public Class<Product> getMappedClass() {
        return Product.class;
    }
}
