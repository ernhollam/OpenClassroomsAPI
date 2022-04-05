package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.MedicalRecord;
import org.springframework.data.repository.CrudRepository;

public interface MedicalRecordRepository extends CrudRepository<MedicalRecord, Long> {
}
