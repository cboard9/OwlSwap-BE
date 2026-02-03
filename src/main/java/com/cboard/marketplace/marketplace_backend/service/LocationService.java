package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.LocationDao;
import com.cboard.marketplace.marketplace_backend.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService
{
    @Autowired
    LocationDao dao;
    public Location findByName(String name)
    {
        return dao.findByName(name);
    }
    public Location findById(Integer locationId) { return dao.findByLocationId(locationId);}
}
