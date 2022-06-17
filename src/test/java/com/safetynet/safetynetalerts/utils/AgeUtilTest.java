package com.safetynet.safetynetalerts.utils;

import com.safetynet.safetynetalerts.exceptions.IllegalValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AgeUtilTest {
    public final static LocalDate LOCAL_DATE_NOW = LocalDate.of(2022, 6, 13);

    @Autowired
    private AgeUtil ageUtil;

    @MockBean
    private Clock clock;

    @BeforeEach
    public void init() {
        // configure a fixed clock to have fixe LocalDate.now()
        Clock fixedClock = Clock.fixed(LOCAL_DATE_NOW.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                                       ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void calculateAge_shouldReturn_rightAge() {
        LocalDate birthdate = LocalDate.of(1980, 12, 25);
        int       age       = ageUtil.calculateAge(birthdate);
        assertThat(age).isEqualTo(42);
    }

    @Test
    void calculateAge_shouldThrow_IllegalValueException_whenBirthdateIsInTheFuture() {
        LocalDate birthdate = LocalDate.of(2025, 12, 25);
        assertThrows(IllegalValueException.class, () -> ageUtil.calculateAge(birthdate));
    }

    @Test
    void calculateAge_shouldThrow_IllegalValueException_whenBirthdateIsNull() {
        assertThrows(IllegalValueException.class, () -> ageUtil.calculateAge(null));
    }

    @Test
    void isChild_shouldReturn_false() {
        LocalDate oldPersonBirthdate = LocalDate.of(1901, 10, 10);
        boolean   isChild            = ageUtil.isChild(oldPersonBirthdate);
        assertFalse(isChild);
    }

    @Test
    void isChild_shouldReturn_true() {
        LocalDate birthdate = LocalDate.now(clock);
        boolean   isChild   = ageUtil.isChild(birthdate);
        assertTrue(isChild);
    }
}