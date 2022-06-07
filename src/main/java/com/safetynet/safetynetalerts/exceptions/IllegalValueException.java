package com.safetynet.safetynetalerts.exceptions;

public class IllegalValueException extends RuntimeException {
    /**
     * Resource not found exception constructor.
     *
     * @param name
     *         Name of illegal data
     * @param value
     *         Value of illegal data
     */
    public IllegalValueException(String name, String value) {
        super("Illegal value (" + value + ") for data " + name);
    }
}
