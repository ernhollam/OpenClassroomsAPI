package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.repository.MedicalRecordRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class MedicalRecordService {

    /**
     * Instance of MedicalRecordRepository.
     */
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * Get medicalRecord.
     *
     * @param id MedicalRecord's id
     *
     * @return MedicalRecord a medicalRecord if not empty
     */
    public Optional<MedicalRecord> getMedicalRecord(final Long id) {
        return medicalRecordRepository.findById(id);
    }

    /**
     * Get the list of all medicalRecords.
     *
     * @return an iterable of MedicalRecords
     */
    public Iterable<MedicalRecord> getMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    /**
     * Delete medicalRecord with given id.
     *
     * @param id ID of medicalRecord to delete
     */
    public void deleteMedicalRecord(final Long id) {
        medicalRecordRepository.deleteById(id);
    }

    /**
     * Save medicalRecord.
     *
     * @param medicalRecord MedicalRecord to save
     *
     * @return MedicalRecord
     */
    public MedicalRecord saveMedicalRecord(final MedicalRecord medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }
}
