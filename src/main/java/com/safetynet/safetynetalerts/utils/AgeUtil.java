package com.safetynet.safetynetalerts.utils;

import com.safetynet.safetynetalerts.constants.PersonConstant;
import com.safetynet.safetynetalerts.exceptions.IllegalValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Computes a person's age. Checks if they are a child or not;
 */
@Component
public final class AgeUtil {
    @Autowired
    private Clock clock;

    public int calculateAge(LocalDate birthdate) throws IllegalValueException {
        LocalDate now = LocalDate.now(clock);
        if (birthdate.isAfter(now)) {
            throw new IllegalValueException("birthdate", birthdate.toString());
        } else {
            return now.getYear() - birthdate.getYear();
        }
    }

    public boolean isChild(LocalDate birthdate) {
        return calculateAge(birthdate) <= PersonConstant.AGE_OF_MAJORITY;
    }
}
