package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Person;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JSonPersonRepository implements PersonRepository {

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
     * Delete person with specified id from JSon file.
     *
     * @param id ID of person to delete
     */
    @Override
    public void deleteById(Long id) {

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
     * Find person with specified ID in JSon file.
     *
     * @param id ID of person to find
     *
     * @return Found person
     */
    @Override
    public Optional<Person> findById(Long id) {
        return Optional.empty();
    }
}
