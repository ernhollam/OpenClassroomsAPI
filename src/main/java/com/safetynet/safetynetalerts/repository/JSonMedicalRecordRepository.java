package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Getter
public class JSonMedicalRecordRepository implements MedicalRecordRepository {

    private final JSonRepository              jSonRepository;
    private final ObjectMapper                medRecordMapper;
    private final Jackson2ObjectMapperBuilder mapperBuilder;

    public JSonMedicalRecordRepository(JSonRepository jSonRepository, Jackson2ObjectMapperBuilder mapperBuilder) {
        this.jSonRepository = jSonRepository;
        this.mapperBuilder = mapperBuilder;
        this.medRecordMapper = this.mapperBuilder.build();
    }


    /**
     * Reads Json file and returns a list of medical records.
     *
     * @return a list of MedicalRecords.
     */
    @Override
    public List<MedicalRecord> findAll() {

        final JsonNode medRecordsNode = jSonRepository.getNode("medicalrecords");

        if (medRecordsNode.isEmpty()) {
            log.error("No medical records found.");
            return Collections.emptyList();
        } else {
            List<MedicalRecord> medicalRecords = medRecordMapper.
                    convertValue(medRecordsNode,
                                 new TypeReference<>() {
                                 }); // Use TypeReference List<MedicalRecord> to avoid casting all the
            // time when this method is called
            log.debug("Found list of medical records: {}", medicalRecords);
            return medicalRecords;
        }
    }

    /**
     * Save medical record into JSon file.
     *
     * @param medicalRecord
     *         Medical record to save
     *
     * @return medical record saved
     */
    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) throws Exception {
        // Get useful nodes
        JsonNode rootNode       = jSonRepository.getNode("root");
        JsonNode medRecordsNode = jSonRepository.getNode("medicalrecords");
        // Transform MedicalRecord object into Json node and add to medicalRecords node
        JsonNode newMedicalRecordAsNode = medRecordMapper.valueToTree(medicalRecord);
        ((ArrayNode) medRecordsNode).add(newMedicalRecordAsNode);
        // Overwrite root node with new medicalrecords node
        updateMedicalRecordsNode((ObjectNode) rootNode, medRecordsNode);
        //Write data
        boolean success = jSonRepository.writeData(rootNode);
        if (success) {
            log.info("Saved new medicalRecord {} {}.", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return medicalRecord;
        } else {
            log.error("Failed to save new medicalRecord {} {}.", medicalRecord.getFirstName(),
                      medicalRecord.getLastName());
            throw new Exception("Failed to save medicalRecord.");
        }
    }


    /**
     * Find medical record with specified name.
     *
     * @param firstName
     *         First name of medical record to find
     * @param lastName
     *         Last name of medical record to find
     *
     * @return Found medical record
     */
    @Override
    public Optional<MedicalRecord> findByName(String firstName, String lastName) {
        List<MedicalRecord> medicalRecords = findAll();
        Optional<MedicalRecord> foundMedicalRecord =
                medicalRecords.stream()
                              .filter(medicalRecord -> medicalRecord.getFirstName().equalsIgnoreCase(firstName)
                                                       && medicalRecord.getLastName().equalsIgnoreCase(lastName))
                              .findFirst();
        log.debug("Found medicalRecord: {}", foundMedicalRecord);
        return foundMedicalRecord;
    }

    /**
     * Delete medicalRecord with specified name from JSon file.
     *
     * @param firstName
     *         First name of medicalRecord to delete
     * @param lastName
     *         Last name of medicalRecord to delete
     */
    @Override
    public void deleteByName(String firstName, String lastName) throws Exception {
        Optional<MedicalRecord> medicalRecordToDelete = findByName(firstName, lastName);
        List<MedicalRecord>     medicalRecords        = findAll();

        if (medicalRecordToDelete.isPresent()) {
            medicalRecords.removeIf(medicalRecord -> medicalRecord.equals(medicalRecordToDelete.get()));
            // update list of medical records in JSON file
            JsonNode medicalRecordsNode = medRecordMapper.valueToTree(medicalRecords);
            JsonNode rootNode           = jSonRepository.getNode("root");
            updateMedicalRecordsNode((ObjectNode) rootNode, medicalRecordsNode);
            boolean success = jSonRepository.writeData(rootNode);
            if (success) {
                log.info("Deleted {} {}'s medical record", firstName, lastName);
            } else {
                log.error("Error when updating JSON file after deletion of {} {}'s medical record",
                          firstName, lastName);
                throw new Exception("Failed to update JSON file after deletion of " + firstName + " " + lastName +
                                    "'s medical record.'");
            }
        } else {
            log.error("{} {}'s medical record does not exist in JSON file. ",
                      firstName, lastName);
            throw new ResourceNotFoundException("The medical record for " + firstName + " " + lastName + " you are " +
                                                "trying to delete does not exist in JSON file.");
        }
    }

    /**
     * Gets person's birthdate.
     *
     * @param firstName
     *         Person's first name.
     * @param lastName
     *         Person's last name.
     *
     * @return Person's birthdate.
     */
    @Override
    public LocalDate getBirthDateByName(String firstName, String lastName) {
        Optional<MedicalRecord> medicalRecord = findByName(firstName, lastName);
        if (medicalRecord.isPresent()) {
            LocalDate birthdate = medicalRecord.get().getBirthdate();
            log.debug(firstName + " " + lastName + "'s birthdate is " + birthdate);
            return birthdate;
        } else {
            String errorMessage = "Medical record for " + firstName + " " + lastName + " was not found. No birthdate" +
                                  " returned.";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }
    }

    /**
     * Gets medications for a given person identified by their name.
     *
     * @param firstName
     *         Person's first name.
     * @param lastName
     *         Person's last name.
     *
     * @return List of medications.
     */
    @Override
    public List<String> getMedicationsByName(String firstName, String lastName) {
        Optional<MedicalRecord> medicalRecord = findByName(firstName, lastName);
        if (medicalRecord.isPresent()) {
            List<String> medications = medicalRecord.get().getMedications();
            log.debug("Medications for " + firstName + " " + lastName + " are: " + medications);
            return medications;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gets allergies for a given person identified by their name.
     *
     * @param firstName
     *         Person's first name.
     * @param lastName
     *         Person's last name.
     *
     * @return List of medications.
     */
    @Override
    public List<String> getAllergiesByName(String firstName, String lastName) {
        Optional<MedicalRecord> medicalRecord = findByName(firstName, lastName);
        if (medicalRecord.isPresent()) {
            List<String> allergies = medicalRecord.get().getAllergies();
            log.debug("Allergies for " + firstName + " " + lastName + " are: " + allergies);
            return allergies;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Overwrites root node with updated list of medicalRecords.
     *
     * @param rootNode
     *         Root node
     * @param updatedMedicalRecordsNode
     *         MedicalRecords node updated
     */
    private void updateMedicalRecordsNode(ObjectNode rootNode, JsonNode updatedMedicalRecordsNode) {
        rootNode.replace("medicalrecords", updatedMedicalRecordsNode);
    }
}
