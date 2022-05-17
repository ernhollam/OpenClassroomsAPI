package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Use this annotation to be able to make setUp() method non-static
public class JSonPersonRepositoryTest {
    private final ObjectMapper         mapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    /**
     * Class under test.
     */
    @Autowired
    private       JSonPersonRepository jsonPersonRepository;
    /**
     * Property data source.
     */
    @Autowired
    private       DataPathProperties   dataPathProperties;
    private       File                 jsonFile;
    private       JsonNode             originalRootNode;
    private       int                  nbPeopleBeforeAnyAction;

    @BeforeAll
    public void setUp() throws IOException {
        JsonNode peopleNode;
        String   jsonPath = dataPathProperties.getDatasource();
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
        // Overwrite original content to JSON data file
        mapper.writeValue(jsonFile, originalRootNode);
    }

    @Test
    public void findAll_shouldReturn_TheListOfAllPeople() {
        //GIVEN JSON data file read in readJsonFile()
        //WHEN calling findAll()
        List<Person> foundPeople = jsonPersonRepository.findAll();
        //THEN there must be six persons in the test file
        assertThat(foundPeople.size()).isEqualTo(nbPeopleBeforeAnyAction);
    }

    @Test
    void save_shouldAddNewPersonInFile() throws Exception {
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
        final JsonNode personsNode = jsonPersonRepository.getJSonRepository().getNode("persons");
        List<Person> actualPeople = mapper.
                convertValue(personsNode,
                             new TypeReference<>() {
                             });
        int result = actualPeople.size();
        assertThat(result).isEqualTo(nbPeopleBeforeAnyAction + 1);
    }

    @Test
    void save_shouldSave_personWithCompleteData() throws Exception {
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
        Optional<Person> savedPerson = jsonPersonRepository.findByName("Tenley", "Boyd");
        assertThat(savedPerson).isPresent();
        Person actualPerson = savedPerson.get();
        assertThat(actualPerson.getAddress()).isEqualTo(TBoyd.getAddress());
        assertThat(actualPerson.getCity()).isEqualTo(TBoyd.getCity());
        assertThat(actualPerson.getZip()).isEqualTo(TBoyd.getZip());
        assertThat(actualPerson.getPhone()).isEqualTo(TBoyd.getPhone());
        assertThat(actualPerson.getEmail()).isEqualTo(TBoyd.getEmail());
    }

    @Test
    void deleteByName_shouldDelete_SpecifiedPersonFromFile_whenPersonExists() throws Exception {
        //GIVEN an existing person in the test data source
        String           firstName      = "Jonanathan";
        String           lastName       = "Marrack";
        Optional<Person> existingPerson = jsonPersonRepository.findByName(firstName, lastName);
        assertThat(existingPerson).isPresent();

        // WHEN calling deleteByName()
        jsonPersonRepository.deleteByName(firstName, lastName);

        //THEN there must be one less person in the file
        Optional<Person> deletedPerson = jsonPersonRepository.findByName(firstName, lastName);
        assertThat(deletedPerson).isEmpty();
    }

    @Test
    void deleteByName_shouldNotDelete_WhenPersonDoesNotExist() {
        //GIVEN a person who does not exist in the test data source
        String           firstName         = "Brian";
        String           lastName          = "Stelzer";
        Optional<Person> nonExistingPerson = jsonPersonRepository.findByName(firstName, lastName);
        assertThat(nonExistingPerson).isEmpty();

        // WHEN calling deleteByName()
        // THEN there must be an exception thrown
        assertThrows(ResourceNotFoundException.class, () -> jsonPersonRepository.deleteByName(firstName, lastName));
    }

    @Test
    void findByName_shouldFindOnePerson_whenPersonExists() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Cooper";
        //WHEN calling findByName()
        Optional<Person> foundPerson = jsonPersonRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertThat(foundPerson).isPresent();
    }

    @Test
    void findByName_shouldFindNoOne_whenPersonDoesNotExist() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Marrack";
        //WHEN calling findByName()
        Optional<Person> foundPerson = jsonPersonRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertThat(foundPerson).isEmpty();
    }

    @Test
    void update_shouldReturn_updatedPerson_whenPersonExists() throws Exception {
        // GIVEN existing  person John Boyd with different address and different phone number
        String newAddress = "112 Steppes Pl";
        String newPhone   = "841-874-9888";
        Person johnBoyd = new Person("John",
                                     "Boyd",
                                     newAddress,
                                     "Culver",
                                     97451,
                                     newPhone,
                                     "jaboyd@email.com");
        // WHEN calling update()
        Person updatedPerson = jsonPersonRepository.update(johnBoyd);
        //THEN
        assertThat(updatedPerson.getAddress()).isEqualTo(newAddress);
        assertThat(updatedPerson.getPhone()).isEqualTo(newPhone);
    }

    @Test
    void update_shouldThrowException_whenPersonDoesNotExist() throws ResourceNotFoundException {
        //GIVEN a person who does not exist in the test data source
        String firstName    = "Brian";
        String lastName     = "Stelzer";
        Person brianStelzer = new Person();
        brianStelzer.setFirstName(firstName);
        brianStelzer.setLastName(lastName);
        Optional<Person> nonExistingPerson = jsonPersonRepository.findByName(firstName, lastName);
        // make sure the person does not exist before running check
        assertThat(nonExistingPerson).isEmpty();

        // WHEN calling update()
        // THEN there must be an exception thrown
        assertThrows(ResourceNotFoundException.class, () -> jsonPersonRepository.update(brianStelzer));
    }

}
