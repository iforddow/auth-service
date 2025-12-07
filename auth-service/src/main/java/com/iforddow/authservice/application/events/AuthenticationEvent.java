package com.iforddow.authservice.application.events;

import com.iforddow.authservice.auth.entity.jpa.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public record AuthenticationEvent(Account account, HttpServletRequest request, HttpServletResponse response) {

    

}
