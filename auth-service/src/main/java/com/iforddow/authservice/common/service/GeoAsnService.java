package com.iforddow.authservice.common.service;

import com.iforddow.authservice.common.records.AsnInfo;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoAsnService {

    private final DatabaseReader databaseReader;

    /**
     * A constructor that initializes the GeoAsnService with the GeoIP2 database.
     *
     * @param database The GeoIP2 database resource.
     * @throws IOException If there is an error reading the database file.
     *
     * @author IFD
     * @since 2025-11-09
     * */
    public GeoAsnService(@Value("classpath:geoip/GeoLite2-ASN.mmdb") Resource database) throws IOException {
        this.databaseReader = new DatabaseReader.Builder(database.getFile()).build();
    }

    public AsnInfo lookup(String ip) {

        String asnNumber = "Unknown";
        String asnOrganization = "Unknown";

        try {
            InetAddress address = InetAddress.getByName(ip);
            AsnResponse response = databaseReader.asn(address);

            if (response == null) {
                return null;
            }

            asnNumber = response.getAutonomousSystemNumber().toString();
            asnOrganization = response.getAutonomousSystemOrganization();

            return new AsnInfo(
                    asnNumber,
                    asnOrganization
            );

        } catch (Exception e) {
            return new AsnInfo(
                    asnNumber,
                    asnOrganization
            );
        }
    }
}
