package com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto;

import com.cboard.marketplace.marketplace_backend.model.*;
import com.cboard.marketplace.marketplace_common.*;
import org.springframework.stereotype.Component;

@Component
public class ServiceToDtoMapper implements ItemToDtoMapper<Service>
{
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
                s.getItemType(),
                s.getImage_name(),
                s.getImage_type(),
                s.getImage_date(),
                s.getDurationMinutes()
        );

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
