package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.model.Person;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
public class JSonPersonRepository implements PersonRepository {

    private static final String       jsonDataPath = "src/test/resources/data.json";
    private static final File         jsonFile     = new File(jsonDataPath);
    private static final ObjectMapper mapper       = new ObjectMapper();
    private static       List<Person> people;

    public JSonPersonRepository() {
        try {
            people = mapper.readValue(jsonFile, new TypeReference<>() {
            });
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Save person into JSon file.
     *
     * @param person Person to save
     *
     * @return person saved
     */
    @Override
    public Person save(Person person) {
        return null;
    }

    /**
     * Delete person with specified name from JSon file.
     *
     * @param firstName First name of person to delete
     * @param lastName  Last name of person to delete
     */
    @Override
    public void deleteByName(String firstName, String lastName) {

    }

    /**
     * Get list of all persons in JSON file.
     *
     * @return list of persons.
     */
    @Override
    public Iterable<Person> findAll() {
        return null;
    }

    /**
     * Find person with specified name.
     *
     * @param firstName First name of person to find
     * @param lastName  Last name of person to find
     *
     * @return Found person
     */
    @Override
    public Optional<Person> findByName(String firstName, String lastName) {
        return Optional.empty();
    }

}
