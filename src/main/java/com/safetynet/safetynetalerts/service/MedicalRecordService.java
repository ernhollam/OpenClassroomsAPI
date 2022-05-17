package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.repository.IMedicalRecordRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class MedicalRecordService implements IMedicalRecordService {

    /**
     * Instance of IMedicalRecordRepository.
     */
    @Autowired
    private IMedicalRecordRepository IMedicalRecordRepository;

    /**
     * Get medicalRecord.
     *
     * @param firstName Person's first name in medical record
     * @param lastName  Person's last name in medical record
     *
     * @return MedicalRecord a medicalRecord if not empty
     */
    @Override
    public Optional<MedicalRecord> getMedicalRecordByName(final String firstName, final String lastName) {
        return IMedicalRecordRepository.findByName(firstName, lastName);
    }

    /**
     * Get the list of all medicalRecords.
     *
     * @return an iterable of MedicalRecords
     */
    @Override
    public Iterable<MedicalRecord> getMedicalRecords() {
        return IMedicalRecordRepository.findAll();
    }

    /**
     * Delete medicalRecord with given id.
     *
     * @param firstName Person's first name in medical record to delete
     * @param lastName  Person's last name in medical record to delete
     */
    @Override
    public void deleteMedicalRecord(final String firstName, final String lastName) throws Exception {
        IMedicalRecordRepository.deleteByName(firstName, lastName);
    }

    /**
     * Save medicalRecord.
     *
     * @param medicalRecord MedicalRecord to save
     *
     * @return MedicalRecord
     */
    @Override
    public MedicalRecord saveMedicalRecord(final MedicalRecord medicalRecord) throws Exception {
        return IMedicalRecordRepository.save(medicalRecord);
    }

    /**
     * Update medical record with given name.
     *
     * @param medicalRecord Person with medical record to update
     */
    @Override
    public MedicalRecord updateMedicalRecord(final MedicalRecord medicalRecord) throws Exception {
        return IMedicalRecordRepository.update(medicalRecord);
    }
}
