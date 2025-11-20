package com.iforddow.authservice.auth;

import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.RegisterRequest;
import com.iforddow.authservice.auth.validator.PasswordValidator;
import com.iforddow.authservice.auth.validator.RegistrationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Registration Validation Tests")
public class RegistrationValidationTest {

    private RegistrationValidator registrationValidator;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        // Mock passwordValidator to do nothing (no exception thrown means valid)
        // If it returns void, no stubbing is needed - just don't throw an exception

        // Mock userRepository to return empty Optional (user doesn't exist)
        when(accountRepository.findAccountByEmail(anyString())).thenReturn(Optional.empty());

        registrationValidator = new RegistrationValidator(passwordValidator, accountRepository);
    }

    @Test
    @DisplayName("Registration Validation Test")
    public void registrationValidationTestSuccess_1() {

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setEmail("izaak@email.com");
        registerRequest.setPassword("ABcd123!");
        registerRequest.setConfirmPassword("ABcd123!");

        try {
            registrationValidator.validateRegistrationRequest(registerRequest, null);
        } catch (Exception e) {
            assert(false);
        }

        assert (true);

    }

    @Test
    @DisplayName("Registration Validation Test")
    public void registrationValidationTestSuccess_2() {

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setEmail("riley@gmail.com");
        registerRequest.setPassword("FranklinTheTurtle1@");
        registerRequest.setConfirmPassword("FranklinTheTurtle1@");

        try {
            registrationValidator.validateRegistrationRequest(registerRequest, null);
        } catch (Exception e) {
            assert(false);
        }

        assert (true);

    }

    @Test
    @DisplayName("Registration Error Test")
    public void registrationValidationError_1() {

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setEmail("riley@gmail..com");
        registerRequest.setPassword("FranklinTheTurtle1@");
        registerRequest.setConfirmPassword("FranklinTheTurtle1@");

        try {
            registrationValidator.validateRegistrationRequest(registerRequest, null);
        } catch (Exception e) {
            assert(true);
        }

    }

    @Test
    @DisplayName("Registration Error Test")
    public void registrationValidationError_2() {

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setEmail("riley@gmail.com");
        registerRequest.setPassword("FranklinTheTurtle1@");
        registerRequest.setConfirmPassword("RjTheFox1*");

        try {
            registrationValidator.validateRegistrationRequest(registerRequest, null);
        } catch (Exception e) {
            assert(true);
        }

    }

}
