package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.safetynet.safetynetalerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JSonPersonRepositoryTest extends JSonRepository {

    /**
     * Class under test.
     */
    @Autowired
    JSonPersonRepository jsonPersonRepository;

    private static JsonNode originalRootNode;

    @BeforeEach
    public JsonNode getOriginalNode(){
        originalRootNode = readJsonFile();
        return originalRootNode;
    }

    @Test
    public void findAll() {
        //GIVEN JSON data file read in readJsonFile()
        //WHEN calling findAll()
        List<Person> foundPeople = (List<Person>) jsonPersonRepository.findAll();
        //THEN there must be six persons in the test file
        assertThat(foundPeople.size()).isEqualTo(6);
    }

    @Test
    void save_personWithCompleteData() {
        //GIVEN a person with complete data to add to the list
        Person TBoyd = new Person();
        TBoyd.setFirstName("Tenley");
        TBoyd.setLastName("Boyd");
        TBoyd.setAddress("1509 Culver St");
        TBoyd.setCity("Culver");
        TBoyd.setZip(97451);
        TBoyd.setPhone("841-874-6512");
        TBoyd.setEmail("tenz@email.com");

        //WHEN calling save()
        jsonPersonRepository.save(TBoyd);

        //THEN
        //TO DO calculer qu'il y a une personne de plus dans la liste par rapport à la originalRootNode
    }

    @Test
    void deleteByName() {
    }

    @Test
    void findByName_whenPersonExistsInJSONFile() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Cooper";
        //WHEN calling findByName()
        Optional<Person> foundPerson = jsonPersonRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertTrue(foundPerson.isPresent());
    }

    @Test
    void findByName_whenPersonDoesNotExistInJSONFile() {
        //GIVEN an existing person in the test data source
        String firstName = "Lily";
        String lastName  = "Marrack";
        //WHEN calling findByName()
        Optional<Person> foundPerson = jsonPersonRepository.findByName(firstName, lastName);
        //THEN there must be six persons in the test file
        assertFalse(foundPerson.isPresent());
    }
}