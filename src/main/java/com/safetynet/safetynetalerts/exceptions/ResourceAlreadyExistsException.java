package com.safetynet.safetynetalerts.exceptions;

/**
 * Resource already exists exception.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Resource already exists exception constructor.
     *
     * @param message Exception message.
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
