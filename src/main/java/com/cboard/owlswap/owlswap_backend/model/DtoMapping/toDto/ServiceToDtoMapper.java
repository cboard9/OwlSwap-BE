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
                s.getUser().getUserId(),
                (s.getCategory() != null) ? s.getCategory().getName() : null,
                s.getReleaseDate(),
                s.isAvailable(),
                (s.getLocation() != null) ? s.getLocation().getName() : null,
                (s.getLocation() != null) ? s.getLocation().getLocationId() : null,
                s.getItemType(),
                null,
                s.getDurationMinutes()
        );

        if (s.getImages() != null) {
            dto.setImages(
                    s.getImages().stream()
                            .map(imageMapper::mapToDto)
                            .toList()
            );
        }

        return dto;
    }

    @Override
    public Class<Service> getMappedClass() {
        return Service.class;
    }
}
