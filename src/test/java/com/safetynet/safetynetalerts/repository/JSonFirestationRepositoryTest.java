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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void findAll_shouldReturn_TheListOfAllFirestations() {
        //GIVEN JSON data file read in readJsonFile()
        //WHEN calling findAll()
        List<Firestation> foundPeople = jSonFirestationRepository.findAll();
        //THEN there must be six firestations in the test file
        assertThat(foundPeople.size()).isEqualTo(nbFirestationsBeforeAnyAction);
    }

    @Test
    void save_shouldAddNewFirestationInFile() throws Exception {
        //GIVEN a firestation with complete data to add to the list
        String address = "644 Gershwin Cir";
        int stationNumber = 1;
        Firestation firestation = new Firestation(address, stationNumber);

        //WHEN calling save()
        jSonFirestationRepository.save(firestation);

        //THEN
        final JsonNode firestationsNode = jSonFirestationRepository.getJSonRepository().getNode("firestations");
        List<Firestation> actualPeople =  mapper.
                convertValue(firestationsNode,
                             new TypeReference<>() {
                             });
        int          result       = actualPeople.size();
        assertThat(result).isEqualTo(nbFirestationsBeforeAnyAction + 1);
    }

    @Test
    void save_shouldSave_firestationWithCompleteData() throws Exception {
        //GIVEN a firestation with complete data to add to the list
        String address = "644 Gershwin Cir";
        int station = 1;
        Firestation firestation = new Firestation(address, station);

        //WHEN calling save()
        jSonFirestationRepository.save(firestation);

        //THEN
        Optional<Firestation> savedFirestation = jSonFirestationRepository.findByStationNumber(station);
        assertThat(savedFirestation).isPresent();
        Firestation actualFirestation = savedFirestation.get();
        assertThat(actualFirestation.getAddress()).isEqualTo(firestation.getAddress());
        assertThat(actualFirestation.getStation()).isEqualTo(firestation.getStation());
    }

    @Test
    void deleteByStationNumber_shouldDelete_SpecifiedFirestationFromFile() throws Exception {
        //GIVEN an existing firestation in the test data source
        int station = 4;
        Optional<Firestation> existingFirestation = jSonFirestationRepository.findByStationNumber(station);
        assertThat(existingFirestation).isPresent();

        // WHEN calling deleteByStationNumber()
        jSonFirestationRepository.deleteByStationNumber(station);

        //THEN there must be one less firestation in the file
        Optional<Firestation> deletedFirestation = jSonFirestationRepository.findByStationNumber(station);
        assertThat(deletedFirestation).isEmpty();
    }

    @Test
    void deleteByName_shouldNotDelete_WhenFirestationDoesNotExist() throws Exception {
        //GIVEN an existing person in the test data source
        int station = 1;
        Optional<Firestation> nonExistingFirestation = jSonFirestationRepository.findByStationNumber(station);
        assertThat(nonExistingFirestation).isEmpty();

        // WHEN calling deleteByStationNumber()
        // THEN there must be an exception thrown
        assertThrows(Exception.class, () -> jSonFirestationRepository.deleteByStationNumber(station));
    }

    @Test
    void findByStationNumber_shouldFindOneFirestation_whenFirestationExists() {
        //GIVEN an existing firestation in the test data source
        int station  = 2;
        //WHEN calling findByStationNumber()
        Optional<Firestation> foundFirestation = jSonFirestationRepository.findByStationNumber(station);
        //THEN there must be six firestations in the test file
        assertThat(foundFirestation).isPresent();
    }

    @Test
    void findByStationNumber_shouldFindNoOne_whenFirestationDoesNotExist() {
        //GIVEN a firestation that does not exist in the test data source
        int station = 1;
        //WHEN calling findByStationNumber()
        Optional<Firestation> foundFirestation = jSonFirestationRepository.findByStationNumber(station);
        //THEN there must be six firestations in the test file
        assertThat(foundFirestation).isEmpty();
    }
}