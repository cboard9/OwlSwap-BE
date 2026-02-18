package com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemImageDto;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import com.cboard.owlswap.owlswap_backend.service.CategoryService;
import com.cboard.owlswap.owlswap_backend.service.LocationService;
import com.cboard.owlswap.owlswap_backend.service.UserService;
import com.cboard.owlswap.owlswap_backend.model.Dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDtoToItemMapper implements DtoToItemMapper<ProductDto>
{
    @Autowired
    CategoryService catService;
    @Autowired
    LocationService locService;
    @Autowired
    UserService userService;
    @Autowired
    DtoToImageMapper imageMapper;

    @Override
    public Item fromDto(ProductDto dto)
    {
        Product p = new Product(
                dto.getItemId(),
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                userService.findById(dto.getUserId()),
                catService.findByName(dto.getCategory()),
                dto.getReleaseDate(),
                dto.isAvailable(),
                locService.findById(dto.getLocationId()),
                dto.getItemType(),
                new ArrayList<>(),
                /*dto.getImage_name(),
                dto.getImage_type(),
                dto.getImage_date(),*/
                dto.getQuantity(),
                dto.getBrand()
        );
        List<ItemImage> images = new ArrayList<>();
        for(ItemImageDto img : dto.getImages())
            images.add(imageMapper.fromDto(img));
        p.setImages(images);
        return p;
    }

    @Override
    public Class<ProductDto> getDtoClass()
    {
        return ProductDto.class;
    }
}
