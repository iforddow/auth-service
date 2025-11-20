package com.iforddow.authservice.auth.request;

import com.iforddow.authservice.common.utility.DeviceType;
import lombok.Data;

/**
* A data class containing the variables to make
* a session token request.
*
* @author IFD
* @since 2025-10-27
* */
@Data
public class SessionTokenRequest {

    private String sessionToken;
    private DeviceType deviceType;

}
