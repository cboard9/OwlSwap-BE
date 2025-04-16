package com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto;


import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_common.*;
import org.springframework.stereotype.Component;

@Component
public class ProductToDtoMapper implements ItemToDtoMapper<Product>
{
    public ProductToDtoMapper() {
    }

    @Override
    public ItemDto mapToDto(Product p)
    {
        ProductDto dto = new ProductDto(
                p.getItemId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getUser().getUserId(),
                p.getCategory().getName(),
                p.getReleaseDate(),
                p.isAvailable(),
                p.getLocation().getName(),
                p.getItemType(),
                p.getImage_name(),
                p.getImage_type(),
                p.getImage_date(),
                p.getQuantity(),
                p.getBrand()
        );
        return dto;
    }

    @Override
    public Class<Product> getMappedClass() {
        return Product.class;
    }
}
