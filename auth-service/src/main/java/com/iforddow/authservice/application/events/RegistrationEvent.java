package com.iforddow.authservice.application.events;

import com.iforddow.authservice.auth.entity.jpa.Account;
import jakarta.servlet.http.HttpServletRequest;

public record RegistrationEvent(Account account, HttpServletRequest request) {
}
