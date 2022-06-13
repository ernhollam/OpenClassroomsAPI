package com.safetynet.safetynetalerts.exceptions;

public class IllegalValueException extends RuntimeException {
    /**
     * Resource not found exception constructor.
     *
     * @param message
     *         Error message
     */
    public IllegalValueException(String message) {
        super(message);
    }
}
