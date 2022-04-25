package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JSonPersonRepositoryTest {


    /**
     * Class under test.
     */
    @Autowired
    JSonPersonRepository jsonPersonRepository;

    @Test
    public void findAll() {
        //GIVEN JSON data file read in readJsonFile()
        //WHEN calling findAll()
        List<Person> foundPeople = (List<Person>) jsonPersonRepository.findAll();
        //THEN there must be six persons in the test file
        assertThat(foundPeople.size()).isEqualTo(6);
    }

    @Test
    void save() {
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