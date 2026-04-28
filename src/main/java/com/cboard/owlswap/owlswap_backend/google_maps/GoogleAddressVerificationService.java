package com.cboard.owlswap.owlswap_backend.google_maps;


import com.cboard.owlswap.owlswap_backend.google_maps.config.GoogleAddressValidationProperties;
import com.cboard.owlswap.owlswap_backend.exception.BadRequestException;
import com.cboard.owlswap.owlswap_backend.model.Dto.VerifiedAddressResultDto;
import com.cboard.owlswap.owlswap_backend.model.Location;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GoogleAddressVerificationService implements AddressVerificationService {

    private final WebClient webClient;
    private final GoogleAddressValidationProperties properties;

    public GoogleAddressVerificationService(GoogleAddressValidationProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl("https://addressvalidation.googleapis.com")
                .build();
    }

    @Override
    public VerifiedAddressResultDto verify(Location location) {
        if (!properties.isEnabled()) {
            return new VerifiedAddressResultDto(
                    location.getAddressLine1(),
                    location.getAddressLine2(),
                    location.getCity(),
                    location.getState(),
                    normalizePostalCode(location.getPostalCode()),
                    location.getCountry() != null ? location.getCountry() : "US",
                    location.getLatitude(),
                    location.getLongitude()
            );
        }

        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new BadRequestException("Address verification is not configured.");
        }

        Map<String, Object> requestBody = Map.of(
                "address", Map.of(
                        "regionCode", location.getCountry() != null ? location.getCountry() : "US",
                        "addressLines", buildAddressLines(location)
                ),
                "enableUspsCass", true
        );

        Map response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1:validateAddress")
                        .queryParam("key", properties.getApiKey())
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return parseGoogleResponse(response);
    }

    private List<String> buildAddressLines(Location location) {
        String line1 = safe(location.getAddressLine1());
        String line2 = safe(location.getAddressLine2());

        String cityStateZip = String.join(" ",
                safe(location.getCity()),
                safe(location.getState()),
                safe(location.getPostalCode())
        ).trim();

        if (!line2.isBlank()) {
            return List.of(line1, line2, cityStateZip);
        }

        return List.of(line1, cityStateZip);
    }

    private VerifiedAddressResultDto parseGoogleResponse(Map response) {
        if (response == null) {
            throw new BadRequestException("Address could not be verified. Please check the address and try again.");
        }

        Map result = (Map) response.get("result");
        if (result == null) {
            throw new BadRequestException("Address could not be verified. Please check the address and try again.");
        }

        Map verdict = (Map) result.get("verdict");
        if (verdict == null) {
            throw new BadRequestException("Address could not be verified. Please check the address and try again.");
        }

        Boolean addressComplete = (Boolean) verdict.get("addressComplete");
        String validationGranularity = (String) verdict.get("validationGranularity");

        if (!Boolean.TRUE.equals(addressComplete)) {
            throw new BadRequestException("Address appears incomplete. Please enter a complete street address.");
        }

        if (validationGranularity == null ||
                !(validationGranularity.equals("PREMISE") || validationGranularity.equals("SUB_PREMISE"))) {
            throw new BadRequestException("Address could not be verified as a real street address.");
        }

        Map address = (Map) result.get("address");
        if (address == null) {
            throw new BadRequestException("Address could not be standardized.");
        }

        List<Map<String, Object>> components =
                (List<Map<String, Object>>) address.get("addressComponents");

        String streetNumber = component(components, "street_number");
        String route = component(components, "route");
        String subpremise = component(components, "subpremise");
        String city = firstNonBlank(
                component(components, "locality"),
                component(components, "postal_town")
        );
        String state = component(components, "administrative_area_level_1");
        String postalCode = component(components, "postal_code");
        String country = component(components, "country");

        String addressLine1 = (streetNumber + " " + route).trim();
        String addressLine2 = subpremise == null || subpremise.isBlank()
                ? null
                : "Unit " + subpremise;

        Double lat = null;
        Double lng = null;

        Map geocode = (Map) result.get("geocode");
        if (geocode != null) {
            Map location = (Map) geocode.get("location");
            if (location != null) {
                lat = toDouble(location.get("latitude"));
                lng = toDouble(location.get("longitude"));
            }
        }

        if (addressLine1.isBlank() || city == null || state == null || postalCode == null) {
            throw new BadRequestException("Address could not be verified. Please check the address and try again.");
        }

        return new VerifiedAddressResultDto(
                addressLine1,
                addressLine2,
                city,
                state,
                normalizePostalCode(postalCode),
                country != null ? country : "US",
                lat,
                lng
        );
    }

    private String component(List<Map<String, Object>> components, String type) {
        if (components == null) {
            return null;
        }

        for (Map<String, Object> component : components) {
            Object componentType = component.get("componentType");

            if (type.equals(componentType)) {
                Map componentName = (Map) component.get("componentName");

                if (componentName != null && componentName.get("text") != null) {
                    return componentName.get("text").toString();
                }
            }
        }

        return null;
    }

    private String normalizePostalCode(String postalCode) {
        if (postalCode == null) {
            return null;
        }

        String trimmed = postalCode.trim();

        if (trimmed.length() >= 5) {
            return trimmed.substring(0, 5);
        }

        return trimmed;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }

        if (b != null && !b.isBlank()) {
            return b;
        }

        return null;
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        return Double.parseDouble(value.toString());
    }
}
