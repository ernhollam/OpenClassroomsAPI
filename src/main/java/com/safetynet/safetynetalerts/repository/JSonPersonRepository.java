package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Getter
public class JSonPersonRepository implements PersonRepository {

    private final JSonRepository              jSonRepository;
    private final Jackson2ObjectMapperBuilder mapperBuilder;
    private final ObjectMapper                personMapper;

    public JSonPersonRepository(JSonRepository jSonRepository, Jackson2ObjectMapperBuilder mapperBuilder) {
        this.jSonRepository = jSonRepository;
        this.mapperBuilder = mapperBuilder;
        this.personMapper = this.mapperBuilder.build();
    }


    /**
     * Reads Json file and returns a list of Person.
     *
     * @return a list of Person.
     */
    @Override
    public List<Person> findAll() {
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
        boolean success = jSonRepository.writeData(rootNode);
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
        List<Person> people = findAll();
        return people.stream()
                     .filter(person -> (person.getFirstName().equalsIgnoreCase(firstName)
                                        && person.getLastName().equalsIgnoreCase(lastName)))
                     .findFirst();
    }

    /**
     * Returns a list of people who live at a given address.
     *
     * @param address
     *         Address to find people at.
     *
     * @return list of Person.
     */
    @Override
    public List<Person> findByAddress(String address) {
        List<Person> people = findAll();
        return people.stream()
                     .filter(person -> person.getAddress().equalsIgnoreCase(address))
                     .collect(Collectors.toList());
    }

    /**
     * Returns a list of people who live in a given city.
     *
     * @param city
     *         City where to find people.
     *
     * @return list of Person.
     */
    @Override
    public List<Person> findByCity(String city) {
        List<Person> persons = findAll();
        return persons.stream()
                      .filter(person -> person.getCity().equalsIgnoreCase(city))
                      .collect(Collectors.toList());
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
        List<Person>     people         = findAll();

        if (personToDelete.isPresent()) {
            people.removeIf(person -> person.equals(personToDelete.get()));
            // update list of persons in JSON file
            JsonNode personsNode = personMapper.valueToTree(people);
            JsonNode rootNode    = jSonRepository.getNode("root");
            updatePersonsNode((ObjectNode) rootNode, personsNode);
            boolean success = jSonRepository.writeData(rootNode);
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
