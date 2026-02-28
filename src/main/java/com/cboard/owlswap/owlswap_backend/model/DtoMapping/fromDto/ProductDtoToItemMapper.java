package com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.context.ItemMappingContext;
import com.cboard.owlswap.owlswap_backend.model.Dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDtoToItemMapper implements DtoToItemMapper<ProductDto>
{

    private final DtoToImageMapper imageMapper;

    public ProductDtoToItemMapper(DtoToImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public Item fromDto(ProductDto dto, ItemMappingContext ctx)
    {

        Product p = new Product(
                dto.getItemId(),
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                ctx.user(),
                ctx.category(),
                dto.getReleaseDate(),
                dto.isAvailable(),
                ctx.location(),
                dto.getItemType(),
                new ArrayList<>(),
                dto.getQuantity(),
                dto.getBrand()
        );

        if (dto.getImages() != null) {
            dto.getImages().stream()
                    .map(imageMapper::fromDto)
                    .forEach(p::addImage); // IMPORTANT: sets image.setItem(this)
        }

        return p;
    }

    @Override
    public Class<ProductDto> getDtoClass()
    {
        return ProductDto.class;
    }
}
