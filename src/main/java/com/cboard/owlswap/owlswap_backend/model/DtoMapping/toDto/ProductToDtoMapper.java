package com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto;


import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.*;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.LocationToDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductToDtoMapper implements ItemToDtoMapper<Product>
{
    private final ImageToDtoMapper imageMapper;
    private final LocationToDtoMapper locationToDtoMapper;

    public ProductToDtoMapper(ImageToDtoMapper imageMapper,
                              LocationToDtoMapper locationToDtoMapper) {
        this.imageMapper = imageMapper;
        this.locationToDtoMapper = locationToDtoMapper;
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

        dto.setListingStatus(p.getListingStatus());
        dto.setReservedUntil(p.getReservedUntil());

        if (p.getLocation() != null) {
            dto.setLocationDto(locationToDtoMapper.toDto(p.getLocation()));
        }

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
