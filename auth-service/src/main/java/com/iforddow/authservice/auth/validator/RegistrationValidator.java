package com.iforddow.authservice.auth.validator;

import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.RegisterRequest;
import com.iforddow.authservice.common.exception.BadRequestException;
import com.iforddow.authservice.common.exception.MultipleIssueException;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * A component to validate account registration requests.
 *
 * @author IFD
 * @since  2025-11-09
 * */
@RequiredArgsConstructor
@Component
public class RegistrationValidator {

    private final PasswordValidator passwordValidator;
    private final AccountRepository accountRepository;

    /**
     * A method to validate the account registration request
     *
     * @author IFD
     * @since 2025-07-22
     *
     */
    public void validateRegistrationRequest(RegisterRequest registerRequest, String existingToken) throws BadRequestException, MultipleIssueException {

        ArrayList<String> errors = new ArrayList<>();

        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String confirmPassword = registerRequest.getConfirmPassword();

        // Simple regex for basic email validation
        String emailRegex = "^(?!.*\\.\\.)(?!\\.)[A-Za-z0-9._%+-]+(?<!\\.)@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (email == null) {
            errors.add("Email is required");
        }   else {
            if(!email.matches(emailRegex)) {
                errors.add("Email format is not valid");
            }
        }

        errors.addAll(passwordValidator.validatePassword(password, confirmPassword));

        // Prevent registration if already logged in
        if(!AuthServiceUtility.isNullOrEmpty(existingToken)) {
            errors.add("Already logged in, log out to register a new account.");
        }

        // Check to ensure the account doesn't exist
        accountRepository.findAccountByEmail(registerRequest.getEmail()).ifPresent(
                account -> {
                    errors.add("An account with this email already exists.");
                }
        );

        if(!errors.isEmpty()) {

            if(errors.size() > 1) {
                throw new MultipleIssueException("Registration failed: " + String.join(", ", errors));
            }   else {
                throw new BadRequestException("Registration failed: " + errors.getFirst());
            }

        }

    }

}
