package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.MedicalRecord;

import java.util.Optional;

public interface MedicalRecordRepository {
    /**
     * Save medical record.
     *
     * @param medicalRecord
     *         Medical record to save
     *
     * @return medical record saved
     */
    MedicalRecord save(MedicalRecord medicalRecord) throws Exception;

    /**
     * Delete medical record with specified id.
     *
     * @param firstName First name of medical record to delete
     * @param lastName  Last name of medical record to delete
     */
    void deleteByName(String firstName, String lastName) throws Exception;

    /**
     * Get list of all medical records.
     *
     * @return list of medical records.
     */
    Iterable<MedicalRecord> findAll();

    /**
     * Find medical record with specified ID.
     *
     * @param firstName First name of medical record to find
     * @param lastName  Last name of medical record to find
     *
     * @return Found medical record
     */
    Optional<MedicalRecord> findByName(String firstName, String lastName);
}
