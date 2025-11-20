package com.iforddow.authservice.auth.request;

import lombok.Data;

/**
 * A request object for changing an account's password.
 * It contains the old password, the new password,
 * and a confirmation of the new password.
 *
 * @author IFD
 * @since 2025-11-09
 * */
@Data
public class ChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

}
