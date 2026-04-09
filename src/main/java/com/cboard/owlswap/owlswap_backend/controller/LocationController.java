package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.model.Dto.CreateSellerLocationRequest;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDto;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDtoOLD;
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


}