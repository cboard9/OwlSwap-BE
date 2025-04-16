package com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_common.*;
import org.springframework.stereotype.Component;

@Component
public class ServiceToDtoMapper implements ItemToDtoMapper<Service>
{
    @Override
    public ItemDto mapToDto(Service s)
    {
        ServiceDto dto = new ServiceDto(
                s.getItemId(),
                s.getName(),
                s.getDescription(),
                s.getPrice(),
                s.getUser().getUserId(),
                s.getCategory().getName(),
                s.getReleaseDate(),
                s.isAvailable(),
                s.getLocation().getName(),
                s.getItemType(),
                s.getImage_name(),
                s.getImage_type(),
                s.getImage_date(),
                s.getDurationMinutes()
        );
        return dto;
    }

    @Override
    public Class<Service> getMappedClass() {
        return Service.class;
    }
}
