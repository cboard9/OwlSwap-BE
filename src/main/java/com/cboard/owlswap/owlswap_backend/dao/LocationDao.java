package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.Dto.LocationType;
import com.cboard.owlswap.owlswap_backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationDao extends JpaRepository<Location, Integer>
{
    public Location findByName(String name);
    public Location findByLocationId(Integer locationId);
    List<Location> findByUser_UserIdAndActiveTrue(Integer userId);

    Optional<Location> findByLocationIdAndUser_UserId(Integer locationId, Integer userId);
    List<Location> findByLocationTypeAndActiveTrue(LocationType locationType);

    List<Location> findByUser_UserIdAndLocationTypeAndActiveTrue(Integer userId, LocationType locationType);
}
