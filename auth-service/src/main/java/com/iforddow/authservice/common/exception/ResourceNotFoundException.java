package com.iforddow.authservice.common.exception;

/**
 * ResourceNotFound - An exception that should be thrown
 * when an account requests to find a resource that does not exist.
 *
 * @author IFD
 * @since 2025-10-27
 * */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
