package com.iforddow.authservice.auth.entity.entity;

import lombok.Builder;
import lombok.Data;

/**
 * A class representing geographical location information.
 * Includes country, country code, region, and city.
 *
 * @author IFD
 * @since 2025-11-09
 */
@Data
@Builder
public class GeoLocation {

    private String country;
    private String countryCode;
    private String region;
    private String city;

}
