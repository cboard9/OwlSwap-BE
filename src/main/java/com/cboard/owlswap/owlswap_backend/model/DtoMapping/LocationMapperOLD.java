package com.cboard.owlswap.owlswap_backend.model.DtoMapping;

import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDtoOLD;
import com.cboard.owlswap.owlswap_backend.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapperOLD
{

    public LocationDtoOLD locationToDto(Location location) throws IllegalAccessException
    {

        return new LocationDtoOLD(
                location.getLocationId(),
                location.getName(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude()

        );
    }
}
