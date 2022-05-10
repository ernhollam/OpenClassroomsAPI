package com.safetynet.safetynetalerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {

    private String firstName;

    private String lastName;

    private LocalDate birthdate;

    private String[] medications;

    private String[] allergies;

    public boolean equals(MedicalRecord medicalRecord) {
        return (this.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                && this.getLastName().equalsIgnoreCase(medicalRecord.getLastName()));
    }
}
