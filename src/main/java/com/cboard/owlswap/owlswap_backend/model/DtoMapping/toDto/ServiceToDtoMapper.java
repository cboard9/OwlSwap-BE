package com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemImageDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceToDtoMapper implements ItemToDtoMapper<Service>
{
    @Autowired
    ImageToDtoMapper imageMapper;
    @Override
    public ItemDto mapToDto(Service s) {
        ServiceDto dto = new ServiceDto(
                s.getItemId(),
                s.getName(),
                s.getDescription(),
                s.getPrice(),
                (s.getUser() != null) ? s.getUser().getUserId() : -1,
                (s.getCategory() != null) ? s.getCategory().getName() : null,
                s.getReleaseDate(),
                s.isAvailable(),
                (s.getLocation() != null) ? s.getLocation().getName() : null,
                (s.getLocation() != null) ? s.getLocation().getLocationId() : null,
                s.getItemType(),
                new ArrayList<>(),
                /*s.getImage_name(),
                s.getImage_type(),
                s.getImage_date(),*/
                s.getDurationMinutes()
        );

        List<ItemImageDto> images = new ArrayList<>();
        for(ItemImage img : s.getImages())
            images.add(imageMapper.mapToDto(img));
        dto.setImages(images);

        if (s.getLocation() != null) {
            dto.setLocationId(s.getLocation().getLocationId());
        }
        return dto;
    }

    @Override
    public Class<Service> getMappedClass() {
        return Service.class;
    }
}
