package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.safetynet.safetynetalerts.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JSonPersonRepositoryTest {


    private static final ObjectMapper         MAPPER                  = new ObjectMapper();
    private static final String               JSON_PATH               = new DataPathProperties().getDatasource();
    private static final File                 JSON_FILE               = new File(JSON_PATH);
    private static       JsonNode             ORIGINAL_ROOT_NODE;
    private static       int                  nbPeopleBeforeAnyAction = 0;
    /**
     * Class under test.
     */
    @Autowired
    private              JSonPersonRepository jsonPersonRepository;

    @BeforeAll
    public static void setUp() throws IOException {
        JsonNode PEOPLE_NODE;
        try {
            ORIGINAL_ROOT_NODE          = MAPPER.readTree(JSON_FILE);
            PEOPLE_NODE                 = ORIGINAL_ROOT_NODE.get("persons");
            List<Person> originalPeople = MAPPER.convertValue(PEOPLE_NODE, new TypeReference<>() {
            });
            nbPeopleBeforeAnyAction     = originalPeople.size();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void reset() throws IOException {
        // Make sure the JSON file is in its initial state after each test
        // Create an instance of DefaultPrettyPrinter
        ObjectWriter writer = MAPPER.writer(new DefaultPrettyPrinter());
        // Overwrite original content to JSON data file
        writer.writeValue(Paths.get(JSON_PATH).toFile(), ORIGINAL_ROOT_NODE);
    }

    @Test
    public void findAll() {
        //GIVEN JSON data file read in readJsonFile()
        //WHEN calling findAll()
        List<Person> foundPeople = (List<Person>) jsonPersonRepository.findAll();
        //THEN there must be six persons in the test file
        assertThat(foundPeople.size()).isEqualTo(nbPeopleBeforeAnyAction);
    }

    @Test
    void save_personWithCompleteData() {
        //GIVEN a person with complete data to add to the list
        Person TBoyd = new Person("Tenley",
                                  "Boyd",
                                  "1509 Culver St",
                                  "Culver",
                                  97451,
                                  "841-874-6512",
                                  "tenz@email.com");

        //WHEN calling save()
        jsonPersonRepository.save(TBoyd);

        //THEN
        Iterable<Person> actualPeople = jsonPersonRepository.getPeopleFromJsonFile();
        int              result       = ((List<Person>) actualPeople).size();
        assertThat(result).isEqualTo(nbPeopleBeforeAnyAction + 1);
    }

    @Test
    void deleteByName_whenPersonExists() {
        //GIVEN an existing person in the test data source
        String firstName = "Jonanathan";
        String lastName  = "Marrack";

        // WHEN calling deleteByName()
        jsonPersonRepository.findByName(firstName, lastName);

        //THEN there must be one less person in the file
        Iterable<Person> actualPeople = jsonPersonRepository.getPeopleFromJsonFile();
        int              result       = ((List<Person>) actualPeople).size();
        assertThat(result).isEqualTo(nbPeopleBeforeAnyAction - 1);
    }

    @Test
    void findByName_whenPersonExists() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Cooper";
        //WHEN calling findByName()
        Optional<Person> foundPerson = jsonPersonRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertTrue(foundPerson.isPresent());
    }

    @Test
    void findByName_whenPersonDoesNotExist() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Marrack";
        //WHEN calling findByName()
        Optional<Person> foundPerson = jsonPersonRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertFalse(foundPerson.isPresent());
    }
}