package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.LocationDao;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDtoOLD;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.LocationMapperOLD;
import com.cboard.owlswap.owlswap_backend.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService
{
    @Autowired
    LocationDao dao;
    @Autowired
    LocationMapperOLD locationMapperOLD;


    public List<LocationDtoOLD> getAllLocations()
    {
        return dao.findAll()
                .stream()
                .map(loc -> {
                try {
                    return locationMapperOLD.locationToDto(loc);
                } catch (Exception e) {
                    throw new DtoMappingException("Failed to map Location to DTO. locationId=" + loc.getLocationId(), e);
                }
            })
                .toList();

    }
    public LocationDtoOLD getLocationById(Integer locationId)
    {
        Location loc = dao.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location not found. locationId=" + locationId));
        try {
            return locationMapperOLD.locationToDto(loc);
        } catch (Exception e) {
            throw new DtoMappingException("Failed to map Location to DTO. locationId=" + loc.getLocationId(), e);
        }
    }
}
