package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository {

    /**
     * Save person.
     *
     * @param person
     *         Person to save
     *
     * @return person saved
     */
    Person save(Person person) throws Exception;

    /**
     * Returns a list of people who live at a given address.
     *
     * @param address
     *         Address to find people at.
     *
     * @return list of Person.
     */
    List<Person> findByAddress(String address);

    /**
     * Returns a list of people who live in a given city.
     *
     * @param city
     *         City where to find people.
     *
     * @return list of Person.
     */
    List<Person> findByCity(String city);

    /**
     * Delete person with specified name.
     *
     * @param firstName
     *         First name of person to delete
     * @param lastName
     *         Last name of person to delete
     */
    void deleteByName(String firstName, String lastName);

    /**
     * Get list of all persons.
     *
     * @return list of persons.
     */
    List<Person> findAll();

    /**
     * Finds person with specified email.
     *
     * @param firstName First name of person to find
     * @param lastName  Last name of person to find
     *
     * @return Found person
     */
    Optional<Person> findByName(String firstName, String lastName);

}
