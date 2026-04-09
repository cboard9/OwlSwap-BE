package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.model.Dto.LocationDtoOLD;
import com.cboard.owlswap.owlswap_backend.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("location")
//@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private LocationService service;

    @GetMapping("all")
    public ResponseEntity<List<LocationDtoOLD>> getAllLocations()
    {
        List<LocationDtoOLD> locations = service.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("{id}")
    public ResponseEntity<LocationDtoOLD> getLocationById(@PathVariable int id)
    {
        LocationDtoOLD loc = service.getLocationById(id);
        return ResponseEntity.ok(loc);
    }
}