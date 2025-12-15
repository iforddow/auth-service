package com.iforddow.authservice.auth.entity.entity;

import lombok.Data;
import ua_parser.Client;
import ua_parser.Parser;

/**
* A class representing client information parsed from a User-Agent string.
*
* @author IFD
* @since 2025-12-12
* */
@Data
public class ClientInfo {

    String deviceType;
    String osType;
    String osVersion;
    String browserType;
    String browserVersion;

    /**
    * A constructor that parses the User-Agent string to extract client information.
    *
    * @param requestAgent The User-Agent string from the HTTP request header.
    *
    * @author IFD
    * @since 2025-12-12
    * */
    public ClientInfo(String requestAgent) {

        Parser parser = new Parser();
        Client agent = parser.parse(requestAgent);

        this.deviceType = agent.device.family;
        this.osType = agent.os.family;
        this.osVersion = agent.os.major + "." + agent.os.minor + "." + agent.os.patch;
        this.browserType = agent.userAgent.family;
        this.browserVersion = agent.userAgent.major + "." + agent.userAgent.minor + "." + agent.userAgent.patch;

    }

}
