package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.MedicalRecord;

import java.util.Optional;

/**
 * Get, delete or save a medical record from/to a datasource.
 */
public interface MedicalRecordService {
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
    Optional<MedicalRecord> getMedicalRecordByName(final String firstName, final String lastName);

    /**
     * Get the list of all medicalRecords.
     *
     * @return an iterable of MedicalRecords
     */
    Iterable<MedicalRecord> getMedicalRecords();

    /**
     * Delete medicalRecord with given id.
     *
     * @param firstName
     *         Person's first name in medical record to delete
     * @param lastName
     *         Person's last name in medical record to delete
     */
    void deleteMedicalRecord(final String firstName, final String lastName) throws Exception;

    /**
     * Save medicalRecord.
     *
     * @param medicalRecord
     *         MedicalRecord to save
     *
     * @return MedicalRecord
     */
    MedicalRecord saveMedicalRecord(final MedicalRecord medicalRecord) throws Exception;

    /**
     * Update medical record with given name.
     *
     * @param medicalRecord
     *         Person with medical record to update
     */
    MedicalRecord updateMedicalRecord(final MedicalRecord medicalRecord) throws Exception;
}
