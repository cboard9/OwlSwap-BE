package com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemImageDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.RequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class RequestToDtoMapper implements ItemToDtoMapper<Request>
{
    @Autowired
    ImageToDtoMapper imageMapper;
    @Override
    public ItemDto mapToDto(Request r) {
        RequestDto dto = new RequestDto(
                r.getItemId(),
                r.getName(),
                r.getDescription(),
                r.getPrice(),
                r.getUser().getUserId(),
                r.getCategory() != null ? r.getCategory().getName() : null,
                r.getReleaseDate(),
                r.isAvailable(),
                r.getLocation() != null ? r.getLocation().getName() : null,
                r.getLocation() != null ? r.getLocation().getLocationId() : null,
                r.getItemType(),
                null,
                r.getDeadline()
        );
        if (r.getImages() != null) {
            dto.setImages(
                    r.getImages().stream()
                            .map(imageMapper::mapToDto)
                            .toList()
            );
        }


        return dto;
    }

    @Override
    public Class<Request> getMappedClass() {
        return Request.class;
    }
}
