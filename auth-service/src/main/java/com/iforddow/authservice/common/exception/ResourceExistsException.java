package com.iforddow.authservice.common.exception;

/**
 * ResourceExistsException - An exception that should be thrown
 * when an account requests to create a resource that already exists.
 *
 * @author IFD
 * @since 2025-10-27
 * */
public class ResourceExistsException extends RuntimeException {
    public ResourceExistsException(String message) {
        super(message);
    }
}
