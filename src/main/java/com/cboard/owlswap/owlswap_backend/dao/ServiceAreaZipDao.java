package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.ServiceAreaZip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceAreaZipDao extends JpaRepository<ServiceAreaZip, String> {

    boolean existsByPostalCodeAndActiveTrue(String postalCode);
}
