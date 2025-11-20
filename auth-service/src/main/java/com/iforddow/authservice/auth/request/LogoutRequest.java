package com.iforddow.authservice.auth.request;

import lombok.Data;

/**
* A data class to get information for a
* logout request.
*
* @author IFD
* @since 2025-10-27
* */
@Data
public class LogoutRequest {

    private boolean allDevices;

}
