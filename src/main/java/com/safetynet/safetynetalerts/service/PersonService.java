package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Person;

import java.util.Optional;

/**
 * Get, delete or save a person from/to a datasource.
 */
public interface PersonService {
    /**
     * Get person.
     *
     * @param firstName
     *         Person's first name
     * @param lastName
     *         Person's last name
     *
     * @return Person a person if not empty
     */
    Optional<Person> getPersonByName(final String firstName, final String lastName);

    /**
     * Get the list of all persons.
     *
     * @return an iterable of Persons
     */
    Iterable<Person> getPersons();

    /**
     * Delete person with given name.
     *
     * @param firstName
     *         First name of person to delete
     * @param lastName
     *         Last name of person to delete
     */
    void deletePerson(final String firstName, final String lastName) throws Exception;

    /**
     * Update person with given name.
     *
     * @param person
     *         Person to update
     */
    Person updatePerson(final Person person) throws Exception;

    /**
     * Save person.
     *
     * @param person Person to save
     *
     * @return Person
     */
    Person savePerson(final Person person) throws Exception;
}
