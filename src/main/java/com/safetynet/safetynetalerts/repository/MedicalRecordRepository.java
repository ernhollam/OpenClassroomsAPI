package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.MedicalRecord;

import java.util.Optional;

public interface MedicalRecordRepository {
    /**
     * Save medical record.
     *
     * @param medicalRecord Medical record to save
     *
     * @return medical record saved
     */
    MedicalRecord save(MedicalRecord medicalRecord);

    /**
     * Delete medical record with specified id.
     *
     * @param id ID of medical record to delete
     */
    void deleteById(Long id);

    /**
     * Get list of all medical records.
     *
     * @return list of medical records.
     */
    Iterable<MedicalRecord> findAll();

    /**
     * Find medical record with specified ID.
     *
     * @param id ID of medical record to find
     *
     * @return Found medical record
     */
    Optional<MedicalRecord> findById(Long id);
}
