package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.safetynet.safetynetalerts.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JSonPersonRepository extends JSonRepository implements PersonRepository {

    /**
     * Reads Json file and returns a list of Person.
     *
     * @return a list of Person.
     */
    public Iterable<Person> getPeopleFromJsonFile() {
        JsonNode completeDataFromJsonAsNode = readJsonFile();
        if (completeDataFromJsonAsNode.isEmpty()) {
            log.warn("JSON file is empty of Persons.");
            return Collections.emptyList();
        } else {
            final JsonNode personsNode = completeDataFromJsonAsNode.get("persons");
            List<Person> people = JSonRepository.mapper.
                    convertValue(personsNode,
                                 new TypeReference<>() {
                                 }); // Use TypeReference List<Person> to avoid casting all the
            // time when this method is calledCollections.emptyList();
            log.debug("Found list of people: {}", people);
            return people;
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
        JsonNode newPersonNode = mapper.valueToTree(person);
        writeJsonFile(newPersonNode);
        log.debug("Saved new person {}.", person);
        return person;
    }

    /**
     * Get list of all persons in JSON file.
     *
     * @return list of persons.
     */
    @Override
    public Iterable<Person> findAll() {
        return getPeopleFromJsonFile();
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
        Optional<Person> foundPerson = Optional.empty();
        Iterable<Person> people      = getPeopleFromJsonFile();

        for (Person person : people) {
            if (person.getFirstName().equalsIgnoreCase(firstName)
                && person.getLastName().equalsIgnoreCase(lastName)) {
                foundPerson = Optional.of(person);
                log.debug("Found person: {}", foundPerson);
                break;
            }
        }
        return foundPerson;
    }

    /**
     * Delete person with specified name from JSon file.
     *
     * @param firstName First name of person to delete
     * @param lastName  Last name of person to delete
     */
    @Override
    public void deleteByName(String firstName, String lastName) {
        Optional<Person> personToDelete = findByName(firstName, lastName);
        Iterable<Person> people         = getPeopleFromJsonFile();

        if (personToDelete.isPresent()) {
            Iterator<Person> iterator = people.iterator();
            while (iterator.hasNext()) {
                // browse list and delete if found
                Person person = iterator.next();
                if (person.equals(personToDelete.get())) {
                    iterator.remove();
                }
            }
            // update list of persons in JSON file
            JsonNode updatedPeopleList = mapper.valueToTree(people);
            boolean  success           = writeJsonFile(updatedPeopleList);
            if (success) {
                log.debug("Deleted person: {} {}", firstName, lastName);
            } else {
                log.error("Error when updating JSON file after deletion of Person {} {}",
                          firstName, lastName);
            }
        } else {
            log.warn("Person {} {} does not exist in JSON file. ",
                     firstName, lastName);
        }
    }
}
