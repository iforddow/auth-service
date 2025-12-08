package com.iforddow.authservice.application.events.authentication;

import com.iforddow.authservice.auth.entity.jpa.Account;
import jakarta.servlet.http.HttpServletRequest;

public record AuthenticationFailedEvent(Account account, HttpServletRequest request) {



}
