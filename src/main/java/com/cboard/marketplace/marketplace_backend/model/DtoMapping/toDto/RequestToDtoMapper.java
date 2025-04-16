package com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_common.*;
import org.springframework.stereotype.Component;

@Component
public class RequestToDtoMapper implements ItemToDtoMapper<Request>
{
    @Override
    public ItemDto mapToDto(Request r)
    {
        RequestDto dto = new RequestDto(
                r.getItemId(),
                r.getName(),
                r.getDescription(),
                r.getPrice(),
                r.getUser().getUserId(),
                r.getCategory().getName(),
                r.getReleaseDate(),
                r.isAvailable(),
                r.getLocation().getName(),
                r.getItemType(),
                r.getImage_name(),
                r.getImage_type(),
                r.getImage_date(),
                r.getDeadline()
        );
        return dto;
    }

    @Override
    public Class<Request> getMappedClass() {
        return Request.class;
    }
}
