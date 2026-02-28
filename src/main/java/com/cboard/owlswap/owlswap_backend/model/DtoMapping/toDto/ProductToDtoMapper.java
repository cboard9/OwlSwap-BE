package com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto;


import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductToDtoMapper implements ItemToDtoMapper<Product>
{
    private final ImageToDtoMapper imageMapper;
    public ProductToDtoMapper(ImageToDtoMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public ItemDto mapToDto(Product p) {
        ProductDto dto = new ProductDto(
                p.getItemId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getUser().getUserId(),
                (p.getCategory() != null) ? p.getCategory().getName() : null,
                p.getReleaseDate(),
                p.isAvailable(),
                (p.getLocation() != null) ? p.getLocation().getName() : null,
                (p.getLocation() != null) ? p.getLocation().getLocationId() : null,
                p.getItemType(),
                new ArrayList<>(),
                p.getQuantity(),
                p.getBrand()
        );

        if (p.getImages() != null) {
            dto.setImages(
                    p.getImages().stream()
                            .map(imageMapper::mapToDto)
                            .toList()
            );
        }


        return dto;
    }

    @Override
    public Class<Product> getMappedClass() {
        return Product.class;
    }
}
