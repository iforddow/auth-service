package com.iforddow.authservice.common.exception;

/**
 * A custom exception to indicate unauthorized access attempts.
 *
 * @author IFD
 * @since 2025-11-09
 * */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
