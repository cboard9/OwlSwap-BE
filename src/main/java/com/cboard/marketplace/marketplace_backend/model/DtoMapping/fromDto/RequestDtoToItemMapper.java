package com.cboard.marketplace.marketplace_backend.model.DtoMapping.fromDto;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.service.CategoryService;
import com.cboard.marketplace.marketplace_backend.service.LocationService;
import com.cboard.marketplace.marketplace_backend.service.UserService;
import com.cboard.marketplace.marketplace_common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestDtoToItemMapper implements DtoToItemMapper<RequestDto>
{
    @Autowired
    CategoryService catService;
    @Autowired
    LocationService locService;
    @Autowired
    UserService userService;

    @Override
    public Item fromDto(RequestDto dto)
    {
        Request r = new Request(
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
                dto.getDeadline()
        );
        return r;
    }

    @Override
    public Class<RequestDto> getDtoClass()
    {
        return RequestDto.class;
    }
}
