package com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemImageDto;
import org.springframework.stereotype.Component;

@Component
public class ImageToDtoMapper
{
    public ItemImageDto mapToDto(ItemImage img) {
        ItemImageDto dto = new ItemImageDto(
                img.getImageId(),
                img.getItem().getItemId(),
                img.getImage_name(),
                img.getImage_type(),
                img.getImage_date()
        );

        return dto;
    }
}
