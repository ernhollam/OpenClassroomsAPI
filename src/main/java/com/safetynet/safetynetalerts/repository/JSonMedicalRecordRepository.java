package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JSonMedicalRecordRepository implements MedicalRecordRepository {
    /**
     * Save medical record into JSon file.
     *
     * @param medicalRecord Medical record to save
     *
     * @return medical record saved
     */
    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        return null;
    }

    /**
     * Delete medical record with specified id from JSon file.
     *
     * @param id ID of medical record to delete
     */
    @Override
    public void deleteById(Long id) {

    }

    /**
     * Get list of all medical records in JSON file.
     *
     * @return list of medical records.
     */
    @Override
    public Iterable<MedicalRecord> findAll() {
        return null;
    }

    /**
     * Find medical record with specified ID in JSon file.
     *
     * @param id ID of medical record to find
     *
     * @return Found medical record
     */
    @Override
    public Optional<MedicalRecord> findById(Long id) {
        return Optional.empty();
    }
}
