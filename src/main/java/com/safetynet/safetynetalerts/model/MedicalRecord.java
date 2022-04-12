package com.safetynet.safetynetalerts.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicalRecord {

    private String firstName;

    private String lastName;

    private LocalDate birthdate;

    private String[] medications;

    private String[] allergies;
}
