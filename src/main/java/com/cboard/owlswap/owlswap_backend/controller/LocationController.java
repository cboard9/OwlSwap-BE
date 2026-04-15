package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.model.Dto.CreateSellerLocationRequest;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDtoOLD;
import com.cboard.owlswap.owlswap_backend.model.Dto.UpdateSellerLocationRequest;
import com.cboard.owlswap.owlswap_backend.model.Location;
import com.cboard.owlswap.owlswap_backend.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("location")
//@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("all")
    public ResponseEntity<List<LocationDtoOLD>> getAllLocations()
    {
        List<LocationDtoOLD> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("{id}")
    public ResponseEntity<LocationDtoOLD> getLocationById(@PathVariable int id)
    {
        LocationDtoOLD loc = locationService.getLocationById(id);
        return ResponseEntity.ok(loc);
    }

    @PostMapping("/seller")
    public ResponseEntity<LocationDto> createSellerAddressLocation(
            @Valid @RequestBody CreateSellerLocationRequest request) {

        LocationDto location = locationService.createSellerAddressLocation(request);
        return ResponseEntity.ok(location);
    }

    @GetMapping("/preset")
    public ResponseEntity<List<LocationDto>> getPresetLocations() {
        List<LocationDto> locationDtos = locationService.getPresetLocations();
        return ResponseEntity.ok(locationDtos);
    }

    @GetMapping("/my-addresses")
    public ResponseEntity<List<LocationDto>> getMySellerAddresses() {
        List<LocationDto> locationDtos = locationService.getMySellerAddresses();
        return ResponseEntity.ok(locationDtos);
    }

    @GetMapping("/my-addresses/{id}")
    public ResponseEntity<LocationDto> getMySellerAddress(@PathVariable("id") Integer locationId) {
        LocationDto location = locationService.getMySellerAddress(locationId);
        return ResponseEntity.ok(location);
    }

    @PutMapping("/my-addresses/{id}")
    public ResponseEntity<LocationDto> updateMySellerAddress(@PathVariable("id") Integer locationId,
                                                             @Valid @RequestBody UpdateSellerLocationRequest request) {
        LocationDto updated = locationService.updateMySellerAddress(locationId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/my-addresses/{id}")
    public ResponseEntity<Void> deleteMySellerAddress(@PathVariable("id") Integer locationId) {
        locationService.deleteMySellerAddress(locationId);
        return ResponseEntity.noContent().build();
    }

}