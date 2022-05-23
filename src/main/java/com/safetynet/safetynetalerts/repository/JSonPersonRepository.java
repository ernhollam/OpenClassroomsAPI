package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Data
public class JSonPersonRepository implements PersonRepository {

    private final JSonRepository jSonRepository;
    private final ObjectMapper   personMapper = new ObjectMapper();

    public JSonPersonRepository(JSonRepository jSonRepository) {
        this.jSonRepository = jSonRepository;
    }


    /**
     * Reads Json file and returns a list of Person.
     *
     * @return a list of Person.
     */
    private List<Person> getPeopleFromJsonFile() {
        final JsonNode personsNode = jSonRepository.getNode("persons");

        if (personsNode.isEmpty()) {
            log.error("No people exist in JSON file.");
            return Collections.emptyList();
        } else {
            List<Person> people = personMapper.
                    convertValue(personsNode,
                                 new TypeReference<>() {
                                 }); // Use TypeReference List<Person> to avoid casting all the
            // time when this method is called
            log.debug("Found list of people: {}", people);
            return people;
        }
    }

    /**
     * Save person into JSon file.
     *
     * @param personToSave
     *         Person to save
     *
     * @return person saved
     */
    public Person save(Person personToSave) throws Exception {
        String firstName = personToSave.getFirstName();
        String lastName  = personToSave.getLastName();
        // Get useful nodes
        JsonNode rootNode    = jSonRepository.getNode("root");
        JsonNode personsNode = jSonRepository.getNode("persons");
        // Transform Person object into Json node and add to persons node
        JsonNode newPersonAsNode = personMapper.valueToTree(personToSave);
        ((ArrayNode) personsNode).add(newPersonAsNode);
        // Overwrite root node with new persons node
        updatePersonsNode((ObjectNode) rootNode, personsNode);
        //Write data
        boolean success = jSonRepository.writeJsonFile(rootNode);
        if (success) {
            log.debug("Saved new person {} {}.", firstName, lastName);
            return personToSave;
        } else {
            String saveFailedErrorMessage = "Failed to save person: " + firstName + " " + lastName + ".";
            log.error(saveFailedErrorMessage);
            throw new Exception(saveFailedErrorMessage);
        }
    }

    /**
     * Get list of all persons in JSON file.
     *
     * @return list of persons.
     */
    @Override
    public List<Person> findAll() {
        return getPeopleFromJsonFile();
    }

    /**
     * Find person with specified name.
     *
     * @param firstName
     *         First name of person to find
     * @param lastName
     *         Last name of person to find
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
     * @param firstName
     *         First name of person to delete
     * @param lastName
     *         Last name of person to delete
     */
    @Override
    public void deleteByName(String firstName, String lastName) throws Exception {
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
            JsonNode personsNode = personMapper.valueToTree(people);
            JsonNode rootNode    = jSonRepository.getNode("root");
            updatePersonsNode((ObjectNode) rootNode, personsNode);
            boolean success = jSonRepository.writeJsonFile(rootNode);
            if (success) {
                log.debug("Deleted person: {} {}", firstName, lastName);
            } else {
                log.error("Error when updating JSON file after deletion of Person {} {}",
                          firstName, lastName);
                throw new Exception("Failed to update JSON file after deletion of person " + firstName + " " + lastName + ".");
            }
        } else {
            String notFoundMessage = "Person " + firstName + " " + lastName + " does not exist.";
            log.error(notFoundMessage);
            throw new ResourceNotFoundException(notFoundMessage);
        }
    }

    /**
     * Overwrites root node with updated list of persons.
     *
     * @param rootNode
     *         Root node
     * @param updatedPersonsNode
     *         Persons node updated
     */
    private void updatePersonsNode(ObjectNode rootNode, JsonNode updatedPersonsNode) {
        rootNode.replace("persons", updatedPersonsNode);
    }
}
