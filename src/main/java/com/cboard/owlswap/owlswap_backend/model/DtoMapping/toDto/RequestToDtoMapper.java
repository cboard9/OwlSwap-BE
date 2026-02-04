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
                r.getUser() != null ? r.getUser().getUserId() : -1,
                r.getCategory() != null ? r.getCategory().getName() : null,
                r.getReleaseDate(),
                r.isAvailable(),
                r.getLocation() != null ? r.getLocation().getName() : null,
                r.getLocation() != null ? r.getLocation().getLocationId() : null,
                r.getItemType(),
                new ArrayList<>(),
                /*r.getImage_name(),
                r.getImage_type(),
                r.getImage_date(),*/
                r.getDeadline()
        );
        List<ItemImageDto> images = new ArrayList<>();
        for(ItemImage img : r.getImages())
            images.add(imageMapper.mapToDto(img));
        dto.setImages(images);

        if (r.getLocation() != null) {
            dto.setLocationId(r.getLocation().getLocationId());
        }
        return dto;
    }

    @Override
    public Class<Request> getMappedClass() {
        return Request.class;
    }
}
