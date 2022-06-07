package com.safetynet.safetynetalerts.utils;

import com.safetynet.safetynetalerts.constants.PersonConstant;
import com.safetynet.safetynetalerts.exceptions.IllegalValueException;

import java.time.LocalDate;

/**
 * Computes a person's age. Checks if they are a child or not;
 */
public class AgeUtil {

    public static int calculateAge(LocalDate birthdate) throws IllegalValueException {
        LocalDate now = LocalDate.now();
        if (birthdate.isAfter(now)) {
            throw new IllegalValueException("birthdate", birthdate.toString());
        } else {
            return LocalDate.now().getYear() - birthdate.getYear();
        }
    }

    public static boolean isChild(LocalDate birthdate) {
        return calculateAge(birthdate) <= PersonConstant.AGE_OF_MAJORITY;
    }
}
