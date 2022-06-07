package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.service.JSonMedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    @Autowired
    private JSonMedicalRecordService jSonMedicalRecordService;


    @GetMapping
    public List<MedicalRecord> getMedicalRecords() {
        return jSonMedicalRecordService.getMedicalRecords();
    }

    @GetMapping("/{firstName}/{lastName}")
    public MedicalRecord getMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) {
        return jSonMedicalRecordService.getMedicalRecordByName(firstName, lastName).
                                       orElseThrow(() -> new ResourceNotFoundException(firstName + " " + lastName +
                                                                                       "'s medical record was not " +
                                                                                       "found."));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecord createMedicalRecord(@RequestBody MedicalRecord medicalRecord) throws Exception {
        return jSonMedicalRecordService.saveMedicalRecord(medicalRecord);
    }


    @PutMapping
    public MedicalRecord updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) throws Exception {
        return jSonMedicalRecordService.updateMedicalRecord(medicalRecord);
    }


    @DeleteMapping("/{firstName}/{lastName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) throws Exception {
        jSonMedicalRecordService.deleteMedicalRecord(firstName, lastName);
    }
}
