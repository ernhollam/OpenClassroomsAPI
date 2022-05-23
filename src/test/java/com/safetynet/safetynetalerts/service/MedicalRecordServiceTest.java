package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.repository.JSonMedicalRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class MedicalRecordServiceTest {
    /**
     * Class under test.
     */
    @Autowired
    private MedicalRecordService medicalRecordService;

    @MockBean
    private JSonMedicalRecordRepository jSonMedicalRecordRepository;

    @Test
    void update_shouldReturn_updatedMedicalRecord_whenExists() throws Exception {
        // GIVEN existing medical record John Boyd with added medications
        String firstName = "John";
        String lastName  = "Boyd";
        String[] medications = {"tradoxidine:400mg",
                                "pharmacol:2500mg"};
        String[] allergies = {"peanut"};
        MedicalRecord medicalRecord = new MedicalRecord(firstName,
                                                        lastName,
                                                        LocalDate.of(1984, 3, 6),
                                                        medications,
                                                        allergies);
        when(jSonMedicalRecordRepository.findByName(firstName, lastName)).thenReturn(Optional.of(medicalRecord));
        when(jSonMedicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        // WHEN calling update()
        MedicalRecord updatedMedicalRecord = medicalRecordService.updateMedicalRecord(medicalRecord);

        //THEN
        assertThat(updatedMedicalRecord.getMedications()).isEqualTo(medications);
        assertThat(updatedMedicalRecord.getAllergies()).isEqualTo(allergies);
    }

    @Test
    void update_shouldThrowException_whenDoesNotExist() throws ResourceNotFoundException {
        //GIVEN a medical record which does not exist in the test data source
        String        firstName    = "Brian";
        String        lastName     = "Stelzer";
        MedicalRecord brianStelzer = new MedicalRecord();
        brianStelzer.setFirstName(firstName);
        brianStelzer.setLastName(lastName);
        when(jSonMedicalRecordRepository.findByName(firstName, lastName)).thenReturn(Optional.empty());
        Optional<MedicalRecord> nonExistingMedicalRecord = medicalRecordService.getMedicalRecordByName(firstName,
                                                                                                       lastName);
        // make sure the person does not exist before running check
        assertThat(nonExistingMedicalRecord).isEmpty();

        // WHEN calling update()
        // THEN there must be an exception thrown
        assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.updateMedicalRecord(brianStelzer));
    }
}