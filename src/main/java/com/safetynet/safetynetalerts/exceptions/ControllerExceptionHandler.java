package com.safetynet.safetynetalerts.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception thrown when a resource is not found.
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String resourceNotFoundException(ResourceNotFoundException notFoundException) {
        log.error("Resource not found.", notFoundException);
        return "Resource not found:\n" + notFoundException.getMessage();
    }

    @ExceptionHandler(IllegalValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String illegalValueException(IllegalValueException illegalValueException) {
        log.error("Illegal argument value.", illegalValueException);
        return "Illegal argument value:\n" + illegalValueException.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String returnMessage(Exception exception) {
        log.error("An error occurred.", exception);
        return "An error occurred:\n " + exception.getMessage();
    }
}
