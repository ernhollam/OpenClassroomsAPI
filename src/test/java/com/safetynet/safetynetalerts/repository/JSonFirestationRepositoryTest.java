package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSonFirestationRepositoryTest {

    private ObjectMapper mapper;

    /**
     * Class under test
     */
    @Autowired
    private JSonFirestationRepository jSonFirestationRepository;

    /**
     * Property data source.
     */
    @Autowired
    private DataPathProperties          dataPathProperties;
    private File                        jsonFile;
    private JsonNode                    originalRootNode;
    private int                         nbFirestationsBeforeAnyAction;
    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;

    @BeforeAll
    public void setUp() throws IOException {
        mapper = mapperBuilder.build();

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
        String      address       = "644 Gershwin Cir";
        int         stationNumber = 1;
        Firestation firestation   = new Firestation(address, stationNumber);

        //WHEN calling save()
        jSonFirestationRepository.save(firestation);

        //THEN
        final JsonNode firestationsNode = jSonFirestationRepository.getJSonRepository().getNode("firestations");
        List<Firestation> actualPeople = mapper.
                convertValue(firestationsNode,
                             new TypeReference<>() {
                             });
        int result = actualPeople.size();
        assertThat(result).isEqualTo(nbFirestationsBeforeAnyAction + 1);
    }

    @Test
    void save_shouldSave_firestationWithCompleteData() throws Exception {
        //GIVEN a firestation with complete data to add to the list
        String      address     = "644 Gershwin Cir";
        int         station     = 1;
        Firestation firestation = new Firestation(address, station);

        //WHEN calling save()
        jSonFirestationRepository.save(firestation);

        //THEN
        Optional<Firestation> savedFirestation = jSonFirestationRepository.findByAddress(address);
        assertThat(savedFirestation).isPresent();
        Firestation actualFirestation = savedFirestation.get();
        assertThat(actualFirestation.getAddress()).isEqualTo(firestation.getAddress());
        assertThat(actualFirestation.getStation()).isEqualTo(firestation.getStation());
    }

    @Test
    void findByAddress_shouldReturn_EmptyOptional() {
        String address = "908 73rd St";

        Optional<Firestation> firestation = jSonFirestationRepository.findByAddress(address);

        assertThat(firestation).isEmpty();
    }

    @Test
    void findByAddress_shouldReturn_OneFirestation() {
        String address = "489 Manchester St";

        Optional<Firestation> firestation = jSonFirestationRepository.findByAddress(address);

        assertThat(firestation).isPresent();
    }

    @Test
    void findByStationNumber_shouldFindThreeFirestations() {
        //GIVEN an existing firestation in the test data source
        int station = 2;
        //WHEN calling findByStationNumber()
        List<Firestation> foundFirestation = jSonFirestationRepository.findByStationNumber(station);
        //THEN there must be two firestations in the test file
        assertThat(foundFirestation.size()).isEqualTo(3);
    }

    @Test
    void findByStationNumber_shouldFindNoOne_whenFirestationDoesNotExist() {
        //GIVEN a firestation that does not exist in the test data source
        int station = 1;
        //WHEN calling findByStationNumber()
        List<Firestation> foundFirestation = jSonFirestationRepository.findByStationNumber(station);
        //THEN there must be six firestations in the test file
        assertThat(foundFirestation.size()).isEqualTo(0);
    }

    @Test
    void deleteByAddress_shouldThrow_ResourceNotFoundException_whenAddressDoesNotExist() {
        String address = "908 73rd St";
        assertThrows(ResourceNotFoundException.class, () -> jSonFirestationRepository.deleteByAddress(address));
    }

    @Test
    void deleteByAddress_shouldDeleteStation() throws Exception {
        String address = "489 Manchester St";

        jSonFirestationRepository.deleteByAddress(address);

        Optional<Firestation> deletedFirestation = jSonFirestationRepository.findByAddress(address);
        assertThat(deletedFirestation).isEmpty();
    }

    @Test
    void deleteByAddress_shouldDelete_OneFirestationFromFile() throws Exception {
        String address = "489 Manchester St";

        jSonFirestationRepository.deleteByAddress(address);


        List<Firestation> firestations = jSonFirestationRepository.findAll();
        assertThat(firestations.size()).isEqualTo(nbFirestationsBeforeAnyAction - 1);
    }

    @Test
    void deleteByStationNumber_shouldDeleteThreeFirestations() throws Exception {
        //GIVEN an existing firestation in the test data source
        int               station              = 2;
        List<Firestation> existingFirestations = jSonFirestationRepository.findByStationNumber(station);
        assertThat(existingFirestations.size()).isNotEqualTo(0);

        // WHEN calling deleteByStationNumber()
        jSonFirestationRepository.deleteByStationNumber(station);

        //THEN there must be no fire stations with specified station number anymore
        List<Firestation> deletedFirestation = jSonFirestationRepository.findByStationNumber(station);
        assertThat(deletedFirestation.size()).isEqualTo(0);
    }

    @Test
    void deleteByStationNumber_shouldDeleteOneFireStation() throws Exception {
        //GIVEN an existing firestation in the test data source
        int               station              = 4;
        List<Firestation> existingFirestations = jSonFirestationRepository.findByStationNumber(station);
        assertThat(existingFirestations.size()).isNotEqualTo(0);

        // WHEN calling deleteByStationNumber()
        jSonFirestationRepository.deleteByStationNumber(station);

        //THEN there must be no fire stations with specified station number anymore
        List<Firestation> deletedFirestation = jSonFirestationRepository.findByStationNumber(station);
        assertThat(deletedFirestation.size()).isEqualTo(0);
    }

    @Test
    void deleteByStationNumber_shouldNotDelete_WhenFirestationDoesNotExist() {
        //GIVEN an existing person in the test data source
        int               station                = 1;
        List<Firestation> nonExistingFirestation = jSonFirestationRepository.findByStationNumber(station);
        assertThat(nonExistingFirestation).isEqualTo(Collections.emptyList());

        // WHEN calling deleteByStationNumber()
        // THEN there must be an exception thrown
        assertThrows(Exception.class, () -> jSonFirestationRepository.deleteByStationNumber(station));
    }


}