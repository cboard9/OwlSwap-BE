package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.LocationDao;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Dto.CreateSellerLocationRequest;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDtoOLD;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationType;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.LocationMapperOLD;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.LocationToDtoMapper;
import com.cboard.owlswap.owlswap_backend.model.Location;
import com.cboard.owlswap.owlswap_backend.model.User;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService
{
    @Autowired
    LocationDao locationDao;
    @Autowired
    LocationMapperOLD locationMapperOLD;
    @Autowired
    LocationValidationService locationValidationService;
    @Autowired
    LocationToDtoMapper locationToDtoMapper;
    @Autowired
    UserDao userDao;
    @Autowired
    CurrentUser currentUser;


    public List<LocationDtoOLD> getAllLocations()
    {
        return locationDao.findAll()
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
        Location loc = locationDao.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location not found. locationId=" + locationId));
        try {
            return locationMapperOLD.locationToDto(loc);
        } catch (Exception e) {
            throw new DtoMappingException("Failed to map Location to DTO. locationId=" + loc.getLocationId(), e);
        }
    }

    @Transactional
    public LocationDto createSellerAddressLocation(CreateSellerLocationRequest request) {
        Integer userId = currentUser.userId();

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        Location location = new Location();
        location.setName(request.getName());
        location.setAddressLine1(request.getAddressLine1());
        location.setAddressLine2(request.getAddressLine2());
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setPostalCode(request.getPostalCode());
        location.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());

        location.setLocationType(LocationType.SELLER_ADDRESS);
        location.setPreset(false);
        location.setActive(true);
        location.setVerified(false); // validation service will set to true if allowed

        locationValidationService.validateForUse(location);

        locationDao.save(location);

        return locationToDtoMapper.toDto(location);
    }
}
