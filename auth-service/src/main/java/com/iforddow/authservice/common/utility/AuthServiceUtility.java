package com.iforddow.authservice.common.utility;

import com.iforddow.authservice.common.exception.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
* A utility class implementing useful methods that
* can be used throughout the application.
*
* @author IFD
* @since 2025-10-27
* */
public class AuthServiceUtility {

    /**
    * A method to check if a String is null or empty.
    *
    * @author IFD
    * @since 2025-10-27
    * */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * A private method to get the currently authenticated account ID.
     *
     * @return The UUID of the currently authenticated account.
     *
     * @author IFD
     * @since 2025-12-03
     * */
    public static UUID getAuthentication() {
        // Get the currently authenticated account ID
        if(SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            throw new BadRequestException("No authentication information found.");
        }

        // Get account ID from security context
        UUID accountId;

        try {
            accountId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        } catch (Exception e) {
            throw new BadRequestException("Invalid authentication information found.");
        }

        return accountId;
    }

}
