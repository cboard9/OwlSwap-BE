package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.ServiceAreaZipDao;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.google_maps.AddressVerificationService;
import com.cboard.owlswap.owlswap_backend.model.Dto.VerifiedAddressResultDto;
import com.cboard.owlswap.owlswap_backend.model.Location;
import com.cboard.owlswap.owlswap_backend.model.Dto.LocationType;
import com.cboard.owlswap.owlswap_backend.model.User;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class LocationValidationService {

    private final ServiceAreaZipDao serviceAreaZipDao;
    private final CurrentUser currentUser;
    private final AddressVerificationService addressVerificationService;

    public LocationValidationService(ServiceAreaZipDao serviceAreaZipDao,
                                     CurrentUser currentUser,
                                     AddressVerificationService addressVerificationService) {
        this.serviceAreaZipDao = serviceAreaZipDao;
        this.currentUser = currentUser;
        this.addressVerificationService = addressVerificationService;
    }

    public void validateForUse(Location location) {
        if (location == null) {
            throw new BadRequestException("Location is required.");
        }

        if (!location.isActive()) {
            throw new BadRequestException("Location is inactive.");
        }

        if (location.getLocationType() == LocationType.PRESET_MEETUP) {
            validatePresetMeetup(location);
            return;
        }

        if (location.getLocationType() == LocationType.SELLER_ADDRESS) {
            if(location.isVerified())
                return;
            validateSellerAddress(location);
            return;
        }

        throw new BadRequestException("Unsupported location type.");
    }

    private void validatePresetMeetup(Location location) {
        if (!location.isPreset()) {
            throw new BadRequestException("Preset meetup location is not marked as preset.");
        }

        if (!location.isVerified()) {
            throw new BadRequestException("Preset meetup location is not verified.");
        }
    }

    private void validateSellerAddress(Location location) {
        if (isBlank(location.getAddressLine1())) {
            throw new BadRequestException("Address line 1 is required for seller addresses.");
        }

        if (isBlank(location.getCity())) {
            throw new BadRequestException("City is required for seller addresses.");
        }

        if (isBlank(location.getState())) {
            throw new BadRequestException("State is required for seller addresses.");
        }

        if (isBlank(location.getPostalCode())) {
            throw new BadRequestException("Postal code is required for seller addresses.");
        }

        String normalizedZip = normalizePostalCode(location.getPostalCode());

        if (!normalizedZip.matches("\\d{5}")) {
            throw new BadRequestException("Postal code must be a valid 5-digit ZIP code.");
        }

        if (!serviceAreaZipDao.existsByPostalCodeAndActiveTrue(normalizedZip)) {
            throw new BadRequestException("This address is outside the supported local service area.");
        }

        location.setPostalCode(normalizedZip);

        VerifiedAddressResultDto verifiedAddress = addressVerificationService.verify(location);

        String verifiedZip = normalizePostalCode(verifiedAddress.getPostalCode());

        if (!serviceAreaZipDao.existsByPostalCodeAndActiveTrue(verifiedZip)) {
            throw new BadRequestException("Verified address is outside the supported local service area.");
        }

        location.setAddressLine1(verifiedAddress.getAddressLine1());
        location.setAddressLine2(
                verifiedAddress.getAddressLine2() != null
                        ? verifiedAddress.getAddressLine2()
                        : location.getAddressLine2()
        );
        location.setCity(verifiedAddress.getCity());
        location.setState(verifiedAddress.getState());
        location.setPostalCode(verifiedZip);
        location.setCountry(verifiedAddress.getCountry());
        location.setLatitude(verifiedAddress.getLatitude());
        location.setLongitude(verifiedAddress.getLongitude());
        location.setVerified(true);
    }

    public void validateOwnershipForUse(Location location, User currentUser)
    {
        if (location == null) {
            throw new BadRequestException("Location is required.");
        }

        if (location.getLocationType() == LocationType.PRESET_MEETUP) {
            return;
        }

        if (location.getLocationType() == LocationType.SELLER_ADDRESS) {
            if (location.getUser() == null) {
                throw new BadRequestException("Seller address location is missing an owner.");
            }

            if (currentUser == null || !location.getUser().getUserId().equals(currentUser.getUserId())) {
                throw new AccessDeniedException("You do not have permission to use this seller address location.");
            }

            return;
        }

        throw new BadRequestException("Unsupported location type.");

    }

    public String normalizePostalCode(String postalCode) {
        if (postalCode == null) {
            return null;
        }
        return postalCode.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
