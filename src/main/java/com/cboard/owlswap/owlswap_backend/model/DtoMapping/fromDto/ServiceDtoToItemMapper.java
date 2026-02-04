package com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemImageDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ServiceDto;
import com.cboard.owlswap.owlswap_backend.service.CategoryService;
import com.cboard.owlswap.owlswap_backend.service.LocationService;
import com.cboard.owlswap.owlswap_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceDtoToItemMapper implements DtoToItemMapper<ServiceDto>
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
    public Item fromDto(ServiceDto dto)
    {
        Service s = new Service(
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
                dto.getDurationMinutes()
        );
        List<ItemImage> images = new ArrayList<>();
        for(ItemImageDto img : dto.getImages())
            images.add(imageMapper.fromDto(img));
        s.setImages(images);
        return s;
    }

    @Override
    public Class<ServiceDto> getDtoClass()
    {
        return ServiceDto.class;
    }
}
