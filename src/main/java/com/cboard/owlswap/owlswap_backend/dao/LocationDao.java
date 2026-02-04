package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationDao extends JpaRepository<Location, Integer>
{
    public Location findByName(String name);
    public Location findByLocationId(Integer locationId);
}
