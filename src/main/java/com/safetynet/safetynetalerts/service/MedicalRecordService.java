package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.repository.MedicalRecordRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class MedicalRecordService implements IMedicalRecordService {

    /**
     * Instance of MedicalRecordRepository.
     */
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * Get medicalRecord.
     *
     * @param firstName
     *         Person's first name in medical record
     * @param lastName
     *         Person's last name in medical record
     *
     * @return MedicalRecord a medicalRecord if not empty
     */
    @Override
    public Optional<MedicalRecord> getMedicalRecordByName(final String firstName, final String lastName) {
        return medicalRecordRepository.findByName(firstName, lastName);
    }

    /**
     * Get the list of all medicalRecords.
     *
     * @return an iterable of MedicalRecords
     */
    @Override
    public Iterable<MedicalRecord> getMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    /**
     * Delete medicalRecord with given id.
     *
     * @param firstName
     *         Person's first name in medical record to delete
     * @param lastName
     *         Person's last name in medical record to delete
     */
    @Override
    public void deleteMedicalRecord(final String firstName, final String lastName) throws Exception {
        medicalRecordRepository.deleteByName(firstName, lastName);
    }

    /**
     * Save medicalRecord.
     *
     * @param medicalRecord
     *         MedicalRecord to save
     *
     * @return MedicalRecord
     */
    @Override
    public MedicalRecord saveMedicalRecord(final MedicalRecord medicalRecord) throws Exception {
        return medicalRecordRepository.save(medicalRecord);
    }

    /**
     * Update medical record with given name.
     *
     * @param medicalRecord
     *         Person with medical record to update
     */
    @Override
    public MedicalRecord updateMedicalRecord(final MedicalRecord medicalRecord) throws Exception {
        String firstName = medicalRecord.getFirstName();
        String lastName  = medicalRecord.getLastName();

        Optional<MedicalRecord> medicalRecordInDataSource = medicalRecordRepository.findByName(firstName, lastName);
        if (medicalRecordInDataSource.isEmpty()) {
            throw new ResourceNotFoundException("The medical record for " + firstName + " " + lastName + " does not " +
                                                "exist.");
        } else {
            return medicalRecordRepository.save(medicalRecord);
        }
    }
}
