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
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Use this annotation to be able to make setUp() method non-static
public class JSonPersonRepositoryTest {
    private final ObjectMapper         mapper                  = new ObjectMapper();
    private final DataPathProperties   dataPathProperties      = new DataPathProperties();
    private final JSonRepository jSonRepository = new JSonRepository(dataPathProperties);
    /**
     * Class under test.
     */
    private final JSonPersonRepository jsonPersonRepository    =
            new JSonPersonRepository(jSonRepository);
    private       File                 jsonFile;
    private       JsonNode             originalRootNode;
    private       int                  nbPeopleBeforeAnyAction = 0;

    @BeforeAll
    public void setUp() throws IOException {
        JsonNode peopleNode;
        String   jsonPath = jsonPersonRepository.getJSonRepository().getDatasource();
        jsonFile = new File(jsonPath);
        try {
            originalRootNode = mapper.readTree(jsonFile);
            peopleNode = originalRootNode.get("persons");
            List<Person> originalPeople = mapper.convertValue(peopleNode, new TypeReference<>() {
            });
            nbPeopleBeforeAnyAction = originalPeople.size();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void reset() throws IOException {
        // Make sure the JSON file is in its initial state after each test
        // Create an instance of DefaultPrettyPrinter
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        // Overwrite original content to JSON data file
        writer.writeValue(jsonFile, originalRootNode);
    }

    @Test
    public void findAll() {
        //GIVEN JSON data file read in readJsonFile()
        //WHEN calling findAll()
        List<Person> foundPeople = jsonPersonRepository.findAll();
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
        List<Person> actualPeople = jsonPersonRepository.getPeopleFromJsonFile();
        int          result       = actualPeople.size();
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
        List<Person> actualPeople = jsonPersonRepository.getPeopleFromJsonFile();
        int          result       = actualPeople.size();
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