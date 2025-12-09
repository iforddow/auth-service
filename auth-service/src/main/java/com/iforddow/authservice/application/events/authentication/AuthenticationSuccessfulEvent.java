package com.iforddow.authservice.application.events.authentication;

import com.iforddow.authservice.auth.entity.jpa.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public record AuthenticationSuccessfulEvent(Account account, String ipAddress) {

    

}
