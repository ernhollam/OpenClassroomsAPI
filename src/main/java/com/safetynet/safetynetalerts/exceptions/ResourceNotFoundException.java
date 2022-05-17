package com.safetynet.safetynetalerts.exceptions;

/**
 * Resource not found exception.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Resource not found exception constructor.
     *
     * @param message Exception message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
