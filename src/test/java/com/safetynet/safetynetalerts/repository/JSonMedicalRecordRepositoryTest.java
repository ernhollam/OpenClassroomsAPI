package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Use this annotation to be able to make setUp() method non-static
public class JSonMedicalRecordRepositoryTest {
    private final ObjectMapper mapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

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

    @BeforeAll
    public void setUp() throws IOException {
        // configure mapper to deserialize properly birthdays
        JavaTimeModule        module                = new JavaTimeModule();
        DateTimeFormatter     dateTimeFormatter     = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer(dateTimeFormatter);
        LocalDateSerializer   localDateSerializer   = new LocalDateSerializer(dateTimeFormatter);
        module.addDeserializer(LocalDate.class, localDateDeserializer)
              .addSerializer(localDateSerializer);

        mapper.registerModule(module)
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
        //GIVEN JSON data file read in readJsonFile()
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
                                                        new String[]{"tradoxidine:400mg"},
                                                        new String[]{});

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
                                                        new String[]{"tradoxidine:400mg"},
                                                        new String[]{});

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
    void update_shouldReturn_updatedMedicalRecord_whenExists() throws Exception {
        // GIVEN existing medical record John Boyd with added medications
        String[] medications = {"tradoxidine:400mg",
                                "pharmacol:2500mg"};
        String[] allergies = {"peanut"};
        MedicalRecord medicalRecord = new MedicalRecord("John",
                                                        "Boyd",
                                                        LocalDate.of(1984, 3, 6),
                                                        medications,
                                                        allergies);
        // WHEN calling update()
        MedicalRecord updatedMedicalRecord = jSonMedicalRecordRepository.update(medicalRecord);
        //THEN
        assertThat(updatedMedicalRecord.getMedications()).isEqualTo(medications);
        assertThat(updatedMedicalRecord.getAllergies()).isEqualTo(allergies);
    }

    @Test
    void update_shouldThrowException_whenDoesNotExist() throws ResourceNotFoundException {
        //GIVEN a medical record which does not exist in the test data source
        String        firstName    = "Brian";
        String        lastName     = "Stelzer";
        MedicalRecord brianStelzer = new MedicalRecord();
        brianStelzer.setFirstName(firstName);
        brianStelzer.setLastName(lastName);
        Optional<MedicalRecord> nonExistingMedicalRecord = jSonMedicalRecordRepository.findByName(firstName, lastName);
        // make sure the person does not exist before running check
        assertThat(nonExistingMedicalRecord).isEmpty();

        // WHEN calling update()
        // THEN there must be an exception thrown
        assertThrows(ResourceNotFoundException.class, () -> jSonMedicalRecordRepository.update(brianStelzer));
    }
}