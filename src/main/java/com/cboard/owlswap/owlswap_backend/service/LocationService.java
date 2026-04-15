package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.LocationDao;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Dto.*;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.LocationMapperOLD;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.LocationToDtoMapper;
import com.cboard.owlswap.owlswap_backend.model.Location;
import com.cboard.owlswap.owlswap_backend.model.User;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
        location.setUser(user);

        location.setLocationType(LocationType.SELLER_ADDRESS);
        location.setPreset(false);
        location.setActive(true);
        location.setVerified(false); // validation service will set to true if allowed

        locationValidationService.validateForUse(location);

        locationDao.save(location);

        return locationToDtoMapper.toDto(location);
    }

    @Transactional
    public List<LocationDto> getPresetLocations() {
        List<Location> locations = locationDao.findByLocationTypeAndActiveTrue(LocationType.PRESET_MEETUP);

        return locations
                .stream()
                .map(loc -> {
                            try {
                                return locationToDtoMapper.toDto(loc);
                            } catch (IllegalArgumentException e) {
                                throw new DtoMappingException("Failed to map Location to DTO. locationId=" + loc.getLocationId(), e);
                            }
                        }
                ).toList();
    }

    @Transactional
    public List<LocationDto> getMySellerAddresses() {
        Integer userId = currentUser.userId();
        List<Location> locations = locationDao.findByUser_UserIdAndLocationTypeAndActiveTrue(userId, LocationType.SELLER_ADDRESS);

        return locations
                .stream()
                .map(loc -> {
                            try {
                                return locationToDtoMapper.toDto(loc);
                            } catch (IllegalArgumentException e) {
                                throw new DtoMappingException("Failed to map Location to DTO. locationId=" + loc.getLocationId(), e);
                            }
                        }
                ).toList();
    }

    @Transactional
    public LocationDto getMySellerAddress(Integer locationId) {
        Integer userId = currentUser.userId();
        Location location = locationDao.findByLocationIdAndUser_UserId(locationId, userId)
                .orElseThrow(() -> new NotFoundException("Seller address location not found."));

        return locationToDtoMapper.toDto(location);
    }

    @Transactional
    public LocationDto updateMySellerAddress(Integer locationId, UpdateSellerLocationRequest request) {
        Integer userId = currentUser.userId();

        Location location = locationDao.findByLocationIdAndUser_UserId(locationId, userId)
                .orElseThrow(() -> new NotFoundException("Seller address location not found."));

        if (location.getLocationType() != LocationType.SELLER_ADDRESS) {
            throw new AccessDeniedException("Only seller address locations can be updated here.");
        }

        location.setName(request.getName());
        location.setAddressLine1(request.getAddressLine1());
        location.setAddressLine2(request.getAddressLine2());
        location.setCity(request.getCity());
        location.setState(request.getState());
        location.setPostalCode(request.getPostalCode());
        location.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());


        // Revalidate after changes
        location.setVerified(false);
        locationValidationService.validateForUse(location);
        locationValidationService.validateOwnershipForUse(location, currentUser.user());

         locationDao.save(location);

        return locationToDtoMapper.toDto(location);
    }

    @Transactional
    public void deleteMySellerAddress(Integer locationId) {
        Integer userId = currentUser.userId();

        Location location = locationDao.findByLocationIdAndUser_UserId(locationId, userId)
                .orElseThrow(() -> new NotFoundException("Seller address location not found."));

        if (location.getLocationType() != LocationType.SELLER_ADDRESS) {
            throw new AccessDeniedException("Only seller address locations can be deleted here.");
        }

        // Soft delete
        location.setActive(false);
        locationDao.save(location);
    }

    /*private String buildLegacyAddress(String addressLine1,
                                      String addressLine2,
                                      String city,
                                      String state,
                                      String postalCode,
                                      String country) {

        StringBuilder sb = new StringBuilder();

        if (addressLine1 != null && !addressLine1.isBlank()) {
            sb.append(addressLine1.trim());
        }

        if (addressLine2 != null && !addressLine2.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(addressLine2.trim());
        }

        if (city != null && !city.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city.trim());
        }

        if (state != null && !state.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state.trim());
        }

        if (postalCode != null && !postalCode.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(postalCode.trim());
        }

        if (country != null && !country.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country.trim());
        }

        return sb.toString();
    }*/
    





}
