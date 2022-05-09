package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.safetynet.safetynetalerts.model.Firestation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSonFirestationRepositoryTest {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Class under test
     */
@Autowired
private JSonFirestationRepository jSonFirestationRepository;

    /**
     * Property data source.
     */
    @Autowired
    private DataPathProperties dataPathProperties;
    private File               jsonFile;
    private JsonNode           originalRootNode;
    private int                nbFirestationsBeforeAnyAction;

    @BeforeAll
    public void setUp() throws IOException {
        JsonNode firestationsNode;
        String   jsonPath = dataPathProperties.getDatasource();
        jsonFile = new File(jsonPath);
        try {
            originalRootNode = mapper.readTree(jsonFile);
            firestationsNode = originalRootNode.get("firestations");
            List<Firestation> firestations = mapper.convertValue(firestationsNode, new TypeReference<>() {
            });
            nbFirestationsBeforeAnyAction = firestations.size();
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
    void getFirestationsFromJsonFile() {
    }

    @Test
    void save() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findByStationNumber() {
    }

    @Test
    void deleteByStationNumber() {
    }
}