package com.cboard.owlswap.owlswap_backend.controller;

import com.cboard.owlswap.owlswap_backend.model.Location;
import com.cboard.owlswap.owlswap_backend.dao.LocationDao;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private LocationDao locationRepository;

    @GetMapping("/locations")
    public List<Location> getAllLocations(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);
        return locationRepository.findAll();
    }

    @GetMapping("/locations/{id}")
    public Location getLocationById(@PathVariable int id) {
        return locationRepository.findById(id).orElse(null);
    }
}