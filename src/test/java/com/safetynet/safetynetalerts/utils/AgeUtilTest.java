package com.safetynet.safetynetalerts.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AgeUtilTest {

    @Test
    void calculateAge_shouldReturn_rightAge() {
        LocalDate birthdate = LocalDate.of(1980, 12, 25);
        //TODO comment utiliser une date fixe pour avoir un âge fixe
        int age = AgeUtil.calculateAge(birthdate);

        assertThat(age).isEqualTo(42);
    }

    @Test
    void isChild_shouldReturn_false() {
        LocalDate oldPersonBirthdate = LocalDate.of(1901, 10, 10);

        boolean isChild = AgeUtil.isChild(oldPersonBirthdate);

        assertFalse(isChild);
    }

    @Test
    void isChild_shouldReturn_true() {
        LocalDate birthdate = LocalDate.now();

        boolean isChild = AgeUtil.isChild(birthdate);

        assertFalse(isChild);
    }
}