package com.cboard.marketplace.marketplace_backend.model.DtoMapping.fromDto;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.CategoryService;
import com.cboard.marketplace.marketplace_backend.service.LocationService;
import com.cboard.marketplace.marketplace_backend.service.UserService;
import com.cboard.marketplace.marketplace_common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceDtoToItemMapper implements DtoToItemMapper<ServiceDto>
{
    @Autowired
    CategoryService catService;
    @Autowired
    LocationService locService;
    @Autowired
    UserService userService;

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
                locService.findByName(dto.getLocation()),
                dto.getItemType(),
                dto.getImage_name(),
                dto.getImage_type(),
                dto.getImage_date(),
                dto.getDurationMinutes()
        );
        return s;
    }

    @Override
    public Class<ServiceDto> getDtoClass()
    {
        return ServiceDto.class;
    }
}
