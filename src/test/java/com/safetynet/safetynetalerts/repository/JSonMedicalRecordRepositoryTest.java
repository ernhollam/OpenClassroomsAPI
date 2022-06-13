package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Use this annotation to be able to make setUp() method non-static
public class JSonMedicalRecordRepositoryTest {
    private ObjectMapper mapper;

    /**
     * Class under test.
     */
    @Autowired
    private JSonMedicalRecordRepository jSonMedicalRecordRepository;
    /**
     * Property data source.
     */
    @Autowired
    private DataPathProperties          dataPathProperties;
    private File                        jsonFile;
    private JsonNode                    originalRootNode;
    private int                         nbMedicalRecordsBeforeAnyAction;
    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;

    @BeforeAll
    public void setUp() throws IOException {
        mapper = mapperBuilder.build();
        JsonNode medicalRecordsNode;
        String   jsonPath = dataPathProperties.getDatasource();
        jsonFile = new File(jsonPath);
        try {
            originalRootNode = mapper.readTree(jsonFile);
            medicalRecordsNode = originalRootNode.get("medicalrecords");
            List<MedicalRecord> originalMedicalRecords = mapper.convertValue(medicalRecordsNode, new TypeReference<>() {
            });
            nbMedicalRecordsBeforeAnyAction = originalMedicalRecords.size();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void reset() throws IOException {
        // Make sure the JSON file is in its initial state after each test
        // Overwrite original content to JSON data file
        mapper.writeValue(jsonFile, originalRootNode);
    }

    @Test
    public void findAll_shouldReturn_TheListOfAllMedicalRecords() {
        //GIVEN JSON data file read in readData()
        //WHEN calling findAll()
        List<MedicalRecord> foundMedicalRecords = jSonMedicalRecordRepository.findAll();
        //THEN there must be six persons in the test file
        assertThat(foundMedicalRecords.size()).isEqualTo(nbMedicalRecordsBeforeAnyAction);
    }

    @Test
    public void findAll_shouldReturn_EmptyList_WhenNodeIsEmpty() throws IOException {
        // GIVEN a file with no medical records
        ObjectNode updatedRootNode = originalRootNode.deepCopy();
        updatedRootNode.remove("medicalrecords");
        mapper.writeValue(jsonFile, updatedRootNode);
        // WHEN calling findAll()
        List<MedicalRecord> foundMedicalRecords = jSonMedicalRecordRepository.findAll();
        //THEN there must be six persons in the test file
        assertTrue(foundMedicalRecords.isEmpty());
    }

    @Test
    void save_shouldAddNewMedicalRecordInFile() throws Exception {
        //GIVEN a medical record with complete data to add to the list
        MedicalRecord medicalRecord = new MedicalRecord("Eric",
                                                        "Cadigan",
                                                        LocalDate.of(1945, 6, 8),
                                                        List.of("tradoxidine:400mg"),
                                                        Collections.emptyList());

        //WHEN calling save()
        jSonMedicalRecordRepository.save(medicalRecord);

        //THEN
        final JsonNode medRecordsNode = jSonMedicalRecordRepository.getJSonRepository().getNode("medicalrecords");
        List<MedicalRecord> actualMedicalRecords = mapper.
                convertValue(medRecordsNode,
                             new TypeReference<>() {
                             });
        int result = actualMedicalRecords.size();
        assertThat(result).isEqualTo(nbMedicalRecordsBeforeAnyAction + 1);
    }

    @Test
    void save_shouldSave_medicalRecord() throws Exception {
        //GIVEN a medical record with complete data to add to the list
        MedicalRecord medicalRecord = new MedicalRecord("Eric",
                                                        "Cadigan",
                                                        LocalDate.of(1945, 6, 8),
                                                        List.of("tradoxidine:400mg"),
                                                        Collections.emptyList());

        //WHEN calling save()
        jSonMedicalRecordRepository.save(medicalRecord);

        //THEN
        Optional<MedicalRecord> savedMedicalRecord = jSonMedicalRecordRepository.findByName("Eric", "Cadigan");
        assertThat(savedMedicalRecord).isPresent();
        MedicalRecord actualMedicalRecord = savedMedicalRecord.get();
        assertThat(actualMedicalRecord.getBirthdate()).isEqualTo(medicalRecord.getBirthdate());
        assertThat(actualMedicalRecord.getMedications()).isEqualTo(medicalRecord.getMedications());
        assertThat(actualMedicalRecord.getAllergies()).isEqualTo(medicalRecord.getAllergies());
    }

    @Test
    void deleteByName_shouldDelete_SpecifiedMedicalRecordFromFile_whenMedicalRecordExists() throws Exception {
        //GIVEN an existing person in the test data source
        String                  firstName             = "Jonanathan";
        String                  lastName              = "Marrack";
        Optional<MedicalRecord> existingMedicalRecord = jSonMedicalRecordRepository.findByName(firstName, lastName);
        assertThat(existingMedicalRecord).isPresent();

        // WHEN calling deleteByName()
        jSonMedicalRecordRepository.deleteByName(firstName, lastName);

        //THEN there must be one less person in the file
        Optional<MedicalRecord> deletedMedicalRecord = jSonMedicalRecordRepository.findByName(firstName, lastName);
        assertThat(deletedMedicalRecord).isEmpty();
    }

    @Test
    void deleteByName_shouldNotDelete_WhenMedicalRecordDoesNotExist() {
        //GIVEN a person who does not exist in the test data source
        String                  firstName                = "Brian";
        String                  lastName                 = "Stelzer";
        Optional<MedicalRecord> nonExistingMedicalRecord = jSonMedicalRecordRepository.findByName(firstName, lastName);
        assertThat(nonExistingMedicalRecord).isEmpty();

        // WHEN calling deleteByName()
        // THEN there must be an exception thrown
        assertThrows(Exception.class, () -> jSonMedicalRecordRepository.deleteByName(firstName, lastName));
    }

    @Test
    void findByName_shouldFindOneMedicalRecord_whenMedicalRecordExists() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Cooper";
        //WHEN calling findByName()
        Optional<MedicalRecord> foundMedicalRecord = jSonMedicalRecordRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertThat(foundMedicalRecord).isPresent();
    }

    @Test
    void findByName_shouldFindNoOne_whenMedicalRecordDoesNotExist() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Marrack";
        //WHEN calling findByName()
        Optional<MedicalRecord> foundMedicalRecord = jSonMedicalRecordRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertThat(foundMedicalRecord).isEmpty();
    }

    @Test
    void getBirthDateByName_shouldThrow_ResourceNotFoundException() {
        String firstName = "Lily";
        String lastName  = "Marrack";
        assertThrows(ResourceNotFoundException.class, () -> jSonMedicalRecordRepository.getBirthDateByName(firstName,
                                                                                                           lastName));
    }

    @Test
    void getBirthDateByName_shouldReturn_LocalDate() {
        // GIVEN
        String firstName = "Lily";
        String lastName  = "Cooper";
        // WHEN
        LocalDate birthdate = jSonMedicalRecordRepository.getBirthDateByName(firstName,
                                                                             lastName);
        //THEN
        assertThat(birthdate).isEqualTo(LocalDate.of(1994, 3, 6).toString());
    }

    @Test
    void getMedicationsByName_shouldReturn_theRightListOfMedications() {
        // GIVEN
        String       firstName           = "John";
        String       lastName            = "Boyd";
        List<String> expectedMedications = List.of("aznol:350mg", "hydrapermazol:100mg");
        // WHEN
        List<String> medications = jSonMedicalRecordRepository.getMedicationsByName(firstName,
                                                                                    lastName);
        //THEN
        assertThat(medications).isEqualTo(expectedMedications);
    }

    @Test
    void getMedicationsByName_shouldReturn_emptyList() {
        // GIVEN
        String firstName = "John";
        String lastName  = "Snow";
        // WHEN
        List<String> medications = jSonMedicalRecordRepository.getMedicationsByName(firstName,
                                                                                    lastName);
        //THEN
        assertThat(medications).isEqualTo(Collections.emptyList());
    }

    @Test
    void getAllergiesByName_shouldReturn_theRightListOfAllergies() {
        // GIVEN
        String       firstName         = "Felicia";
        String       lastName          = "Boyd";
        List<String> expectedAllergies = List.of("xilliathal");
        // WHEN
        List<String> allergies = jSonMedicalRecordRepository.getAllergiesByName(firstName,
                                                                                lastName);
        //THEN
        assertEquals(allergies, expectedAllergies);
    }

    @Test
    void getAllergiesByName_shouldReturn_emptyList() {
        // GIVEN
        String firstName = "Lily";
        String lastName  = "Cooper";
        // WHEN
        List<String> allergies = jSonMedicalRecordRepository.getAllergiesByName(firstName,
                                                                                lastName);
        //THEN
        assertThat(allergies).isEqualTo(Collections.emptyList());
    }
}