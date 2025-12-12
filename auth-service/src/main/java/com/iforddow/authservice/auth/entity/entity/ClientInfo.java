package com.iforddow.authservice.auth.entity.entity;

import lombok.Data;
import ua_parser.Client;
import ua_parser.Parser;

@Data
public class ClientInfo {

    String deviceType;
    String osType;
    String osVersion;
    String browserType;
    String browserVersion;

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
