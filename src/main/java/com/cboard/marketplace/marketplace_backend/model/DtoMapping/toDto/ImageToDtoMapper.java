package com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_backend.model.Dto.ItemDto;
import com.cboard.marketplace.marketplace_backend.model.Dto.ItemImageDto;
import com.cboard.marketplace.marketplace_backend.model.Dto.ServiceDto;
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
