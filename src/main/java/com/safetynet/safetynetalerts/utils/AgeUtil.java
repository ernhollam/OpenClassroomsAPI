package com.safetynet.safetynetalerts.utils;

import com.safetynet.safetynetalerts.constants.PersonConstant;
import com.safetynet.safetynetalerts.exceptions.IllegalValueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Computes a person's age. Checks if they are a child or not;
 */
@Component
@Slf4j
public final class AgeUtil {
    @Autowired
    private Clock clock;

    public int calculateAge(LocalDate birthdate) throws IllegalValueException {
        if (birthdate == null) {
            log.error("AgeUtil: Birthdate is null.");
            throw new IllegalValueException("Birthdate can not be empty.");
        } else {
            LocalDate now = LocalDate.now(clock);
            if (birthdate.isAfter(now)) {
                log.error("AgeUtil: Birthdate is in the future.");
                throw new IllegalValueException("Birthdate " + birthdate + "is in the future.");
            } else {
                int age = now.getYear() - birthdate.getYear();
                log.debug("AgeUtil: Age for birthdate " + birthdate + " is " + age + ".");
                return age;
            }
        }
    }

    public boolean isChild(LocalDate birthdate) {
        boolean isChild = calculateAge(birthdate) <= PersonConstant.AGE_OF_MAJORITY;
        log.debug("AgeUtil: A person born in " + birthdate + " is a child: " + isChild);
        return isChild;
    }
}
