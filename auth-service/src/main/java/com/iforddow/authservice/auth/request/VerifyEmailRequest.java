package com.iforddow.authservice.auth.request;

import lombok.Data;

@Data
public class VerifyEmailRequest {

    private String email;
    private String verificationCode;

}
