package com.iforddow.authservice.auth;

import com.iforddow.authservice.auth.validator.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Password Validation Tests")
public class PasswordValidationTest {

    /*
     * We assume a password has the following:
     *
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     * - At least one special character
     * - At least 8 characters long
     *
     * */

    private PasswordValidator passwordValidator;

    @BeforeEach
    public void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    @DisplayName("Password Validation Test")
    public void validatePasswordTest_1() {

        String password = "FranklinTheTurtle1!";

        assert(passwordValidator.validatePassword(password).isEmpty());

    }

    @Test
    @DisplayName("Password Validation Test")
    public void validatePasswordTest_2() {

        String password = "___123Ab!";

        assert(passwordValidator.validatePassword(password).isEmpty());

    }

    @Test
    @DisplayName("Password Validation Error Test")
    public void validatePasswordErrorTest_1() {

        String password = "thisshittypasswordshouldfail";

        assert(!passwordValidator.validatePassword(password).isEmpty());

    }

    @Test
    @DisplayName("Password Validation Error Test")
    public void validatePasswordErrorTest_2() {

        String password = "alongwithonewithnumbers1andspecial!";

        assert(!passwordValidator.validatePassword(password).isEmpty());

    }

    @Test
    @DisplayName("Password Change Validation Test")
    public void validatePasswordChangeTest_1() {

        String oldPassword = "FranklinTheTurtle2!";
        String newPassword = "FranklinTheTurtle1!";
        String confirmPassword = "FranklinTheTurtle1!";

        assert(passwordValidator.validatePassword(oldPassword, newPassword, confirmPassword).isEmpty());

    }

    @Test
    @DisplayName("Password Change Validation Test")
    public void validatePasswordChangeTest_2() {

        String oldPassword = "FranklinTheTurtle1!";
        String newPassword = "BobTheBuilder2000*";
        String confirmPassword = "BobTheBuilder2000*";

        assert(passwordValidator.validatePassword(oldPassword, newPassword, confirmPassword).isEmpty());

    }

    @Test
    @DisplayName("Password Change Validation Test")
    public void validatePasswordChangeErrorTest_1() {

        String oldPassword = "FranklinTheTurtle1!";
        String newPassword = "FranklinTheTurtle1!";
        String confirmPassword = "FranklinTheTurtle1!";

        assert(!passwordValidator.validatePassword(oldPassword, newPassword, confirmPassword).isEmpty());

    }

    @Test
    @DisplayName("Password Change Validation Test")
    public void validatePasswordChangeErrorTest_2() {

        String oldPassword = "FranklinTheTurtle1!";
        String newPassword = "BobTheBuilder2000*";
        String confirmPassword = "BobTheBuilder1999*";

        assert(!passwordValidator.validatePassword(oldPassword, newPassword, confirmPassword).isEmpty());

    }

    @Test
    @DisplayName("Password Registration Validation Test")
    public void validatePasswordRegistrationTest_1() {

        String newPassword = "BobTheBuilder2000*";
        String confirmPassword = "BobTheBuilder2000*";

        assert(passwordValidator.validatePassword(newPassword, confirmPassword).isEmpty());

    }

    @Test
    @DisplayName("Password Registration Validation Test")
    public void validatePasswordRegistrationTest_2() {

        String newPassword = "FranklinTheTurtle1!*";
        String confirmPassword = "FranklinTheTurtle1!*";

        assert(passwordValidator.validatePassword(newPassword, confirmPassword).isEmpty());

    }

    @Test
    @DisplayName("Password Registration Validation Error Test")
    public void validatePasswordRegistrationErrorTest_1() {

        String newPassword = "FranklinTheTurtle2000!*";
        String confirmPassword = "FranklinTheTurtle2000!";

        assert(!passwordValidator.validatePassword(newPassword, confirmPassword).isEmpty());

    }

    @Test
    @DisplayName("Password Registration Validation Error Test")
    public void validatePasswordRegistrationErrorTest_2() {

        String newPassword = "franklinTheTurtle1";
        String confirmPassword = "franklinTheTurtle1";

        assert(!passwordValidator.validatePassword(newPassword, confirmPassword).isEmpty());

    }

}
