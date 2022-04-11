package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Person;

import java.util.Optional;

public interface PersonRepository {

    /**
     * Save person.
     *
     * @param person Person to save
     *
     * @return person saved
     */
    Person save(Person person);

    /**
     * Delete person with specified id.
     *
     * @param id ID of person to delete
     */
    void deleteById(Long id);

    /**
     * Get list of all persons.
     *
     * @return list of persons.
     */
    Iterable<Person> findAll();

    /**
     * Find person with specified ID.
     *
     * @param id ID of person to find
     *
     * @return Found person
     */
    Optional<Person> findById(Long id);
}
