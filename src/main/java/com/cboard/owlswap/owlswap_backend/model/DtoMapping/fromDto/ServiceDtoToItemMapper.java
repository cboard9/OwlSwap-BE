package com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto;

import com.cboard.owlswap.owlswap_backend.dao.LocationDao;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemImageDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ServiceDto;
import com.cboard.owlswap.owlswap_backend.model.context.ItemMappingContext;
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
    LocationDao locDao;
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    @Autowired
    DtoToImageMapper imageMapper;

    @Override
    public Item fromDto(ServiceDto dto, ItemMappingContext ctx)
    {
        Service s = new Service(
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
                dto.getDurationMinutes()
        );

        if (dto.getImages() != null) {
            dto.getImages().stream()
                    .map(imageMapper::fromDto)
                    .forEach(s::addImage); // IMPORTANT: sets image.setItem(this)
        }

        return s;
    }

    @Override
    public Class<ServiceDto> getDtoClass()
    {
        return ServiceDto.class;
    }
}
