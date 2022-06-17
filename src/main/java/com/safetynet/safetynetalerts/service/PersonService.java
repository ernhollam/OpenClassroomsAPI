package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.ChildAlertViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FireViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.PersonInfoViewModel;

import java.util.Optional;
import java.util.Set;

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
     * @param person
     *         Person to save
     *
     * @return Person
     */
    Person savePerson(final Person person) throws Exception;

    /**
     * Gets children living at a given address.
     *
     * @param address
     *         Address where to find children.
     *
     * @return a list of children and other household members living at given address
     */
    ChildAlertViewModel getChildAlert(String address);

    /**
     * Returns a list of people living at a given address, as well as the station number covering the house.
     *
     * @param address
     *         Address where to find people.
     *
     * @return a list of people living at address and the fire station which covers the address.
     */
    FireViewModel getFirePeople(String address);

    /**
     * Returns information about given person and people with the same name.
     *
     * @param firstName
     *         Person's first name.
     * @param lastName
     *         Person's last name
     *
     * @return Information about the person and a list of people with same name.
     */
    PersonInfoViewModel getPersonInfo(String firstName, String lastName);

    /**
     * Returns a list of all email addresses of people living in a city.
     */
    Set<String> getCommunityEmail(String city);
}
