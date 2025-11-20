package com.iforddow.authservice.common.exception;

/**
 * A custom exception to indicate multiple issues encountered during processing.
 *
 * @author IFD
 * @since 2025-11-09
 * */
public class MultipleIssueException extends RuntimeException {
    public MultipleIssueException(String message) {
        super(message);
    }
}
