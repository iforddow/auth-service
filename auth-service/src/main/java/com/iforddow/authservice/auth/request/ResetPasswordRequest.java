package com.iforddow.authservice.auth.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String code;
    private String newPassword;
    private String confirmNewPassword;

}
