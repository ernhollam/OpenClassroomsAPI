package com.safetynet.safetynetalerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {

    private String       firstName;
    private String       lastName;
    private LocalDate    birthdate;
    private List<String> medications;
    private List<String> allergies;
}
