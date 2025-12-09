package com.iforddow.authservice.common.service;

import com.iforddow.authservice.auth.entity.entity.GeoLocation;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

/**
 * A service class for retrieving geolocation information based on IP addresses.
 * Uses the MaxMind GeoIP2 database to fetch location details.
 *
 * @author IFD
 * @since 2025-11-09
 * */
@Service
@Slf4j
public class GeoLocationService {

    private final DatabaseReader databaseReader;

    /**
     * A constructor that initializes the GeoLocationService with the GeoIP2 database.
     *
     * @param database The GeoIP2 database resource.
     * @throws IOException If there is an error reading the database file.
     *
     * @author IFD
     * @since 2025-11-09
     * */
    public GeoLocationService(@Value("classpath:geoip/GeoLite2-City.mmdb") Resource database) throws IOException {
        this.databaseReader = new DatabaseReader.Builder(database.getFile()).build();
    }

    /**
     * A method to get geolocation information for a given IP address.
     *
     * @param ipAddress The IP address to look up.
     * @return A GeoLocation object containing country, region, and city information.
     *
     * @author IFD
     * @since 2025-11-09
     * */
    public GeoLocation getLocation(String ipAddress) {

        try {

            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CityResponse cityResponse = databaseReader.city(inetAddress);

            return GeoLocation.builder()
                    .country(cityResponse.getCountry().getName())
                    .countryCode(cityResponse.getCountry().getIsoCode())
                    .region(cityResponse.getMostSpecificSubdivision().getName())
                    .city(cityResponse.getCity().getName())
                    .build();

        } catch (Exception e) {
            return GeoLocation.builder()
                    .country("Unknown")
                    .countryCode("Unknown")
                    .region("Unknown")
                    .city("Unknown")
                    .build();
        }

    }

}
