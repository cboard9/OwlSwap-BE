package com.cboard.owlswap.owlswap_backend.model.DtoMapping;

import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDto;
import com.cboard.owlswap.owlswap_backend.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationToDtoMapper {

    public LocationDto toDto(Location location) {
        if (location == null) {
            return null;
        }

        LocationDto dto = new LocationDto();
        dto.setLocationId(location.getLocationId());
        dto.setName(location.getName());
        dto.setAddressLine1(location.getAddressLine1());
        dto.setAddressLine2(location.getAddressLine2());
        dto.setCity(location.getCity());
        dto.setState(location.getState());
        dto.setPostalCode(location.getPostalCode());
        dto.setCountry(location.getCountry());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setLocationType(location.getLocationType() != null ? location.getLocationType().name() : null);
        dto.setVerified(location.isVerified());
        dto.setPreset(location.isPreset());
        dto.setActive(location.isActive());

        return dto;
    }
}
