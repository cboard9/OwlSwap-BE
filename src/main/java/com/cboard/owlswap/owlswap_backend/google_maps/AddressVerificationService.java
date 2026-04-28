package com.cboard.owlswap.owlswap_backend.google_maps;

import com.cboard.owlswap.owlswap_backend.model.Dto.VerifiedAddressResultDto;
import com.cboard.owlswap.owlswap_backend.model.Location;

public interface AddressVerificationService {
    VerifiedAddressResultDto verify(Location location);
}
