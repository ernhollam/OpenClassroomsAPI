package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Data
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
     * Reads Json file and returns a list of medical records
     *
     * @return a list of MedicalRecords.
     */
    private List<MedicalRecord> getMedicalRecordsFromJsonFile() {

        final JsonNode medRecordsNode = jSonRepository.getNode("medicalrecords");

        if (medRecordsNode.isEmpty()) {
            log.error("No medical records exist in JSON file.");
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
            log.debug("Saved new medicalRecord {} {}.", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return medicalRecord;
        } else {
            log.error("Failed to save new medicalRecord {} {}.", medicalRecord.getFirstName(),
                      medicalRecord.getLastName());
            throw new Exception("Failed to save medicalRecord.");
        }
    }


    /**
     * Get list of all medical records in JSON file.
     *
     * @return list of medical records.
     */
    @Override
    public List<MedicalRecord> findAll() {
        return getMedicalRecordsFromJsonFile();
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
        Optional<MedicalRecord> foundMedicalRecord = Optional.empty();
        Iterable<MedicalRecord> medicalRecords     = getMedicalRecordsFromJsonFile();

        for (MedicalRecord medicalRecord : medicalRecords) {
            if (medicalRecord.getFirstName().equalsIgnoreCase(firstName)
                && medicalRecord.getLastName().equalsIgnoreCase(lastName)) {
                foundMedicalRecord = Optional.of(medicalRecord);
                log.debug("Found medicalRecord: {}", foundMedicalRecord);
                break;
            }
        }
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
        Optional<MedicalRecord> medicalRecordToDelete      = findByName(firstName, lastName);
        Iterable<MedicalRecord> medicalRecordsFromJsonFile = getMedicalRecordsFromJsonFile();

        if (medicalRecordToDelete.isPresent()) {
            Iterator<MedicalRecord> iterator = medicalRecordsFromJsonFile.iterator();
            while (iterator.hasNext()) {
                // browse list and delete if found
                MedicalRecord medicalRecord = iterator.next();
                if (medicalRecord.equals(medicalRecordToDelete.get())) {
                    iterator.remove();
                }
            }
            // update list of medical records in JSON file
            JsonNode medicalRecordsNode = medRecordMapper.valueToTree(medicalRecordsFromJsonFile);
            JsonNode rootNode           = jSonRepository.getNode("root");
            updateMedicalRecordsNode((ObjectNode) rootNode, medicalRecordsNode);
            boolean success = jSonRepository.writeData(rootNode);
            if (success) {
                log.debug("Deleted {} {}'s medical record", firstName, lastName);
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
                                                "trying to " +
                                                "delete " +
                                                "does" +
                                                " not " +
                                                "exist in JSON file.");
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
            return medicalRecord.get().getBirthdate();
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
            return medicalRecord.get().getMedications();
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
            return medicalRecord.get().getAllergies();
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
