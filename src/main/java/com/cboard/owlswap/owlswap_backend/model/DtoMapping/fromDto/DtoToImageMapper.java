package com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto;

import com.cboard.owlswap.owlswap_backend.model.*;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemImageDto;
import org.springframework.stereotype.Component;

@Component
public class DtoToImageMapper
{
    public DtoToImageMapper() {
    }

/*    public ItemImage fromDto(ItemImageDto img, Item item) {
        ItemImage image = new ItemImage();
        image.setImageId(img.getImageId());
        image.setImage_name(img.getImage_name());
        image.setImage_type(img.getImage_type());
        image.setImage_date(img.getImage_date());
        image.setItem(item);

        return image;
    }*/

    public ItemImage fromDto(ItemImageDto img) {
        ItemImage image = new ItemImage();
        image.setImageId(img.getImageId());
        image.setImage_name(img.getImage_name());
        image.setImage_type(img.getImage_type());
        image.setImage_date(img.getImage_date());

        return image;
    }
}
