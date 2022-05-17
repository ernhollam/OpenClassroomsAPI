package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Collections.EMPTY_LIST;

@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Read - Get all persons from date file
     *
     * @return HTTP response with: - no content (204) when list is empty - ok (200) when a list of people or a person is
     *         found - not found (404) when the person is not found - body: list of people or one Person
     */
    @GetMapping
    public ResponseEntity<Iterable<Person>> getPersons(@RequestParam(name = "firstName", required = false) String firstName,
                                                       @RequestParam(name = "lastName", required = false) String lastName) {
        boolean firstNameProvided   = firstName != null;
        boolean lastNameProvided    = lastName != null;
        boolean callGetPersonByName = firstNameProvided && lastNameProvided;
        boolean callGetPersons      = !firstNameProvided && !lastNameProvided;
        if (callGetPersons) {
            log.debug("Person Controller: GET/person");
            Iterable<Person> people = personService.getPersons();
            if (people.equals(EMPTY_LIST)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(people, HttpStatus.OK);
            }
        } else if (callGetPersonByName) {
            log.debug("Person Controller: GET/person?firstName={}&lastName={}", firstName, lastName);
            Person person = personService.getPersonByName(firstName, lastName).
                                         orElseThrow(() -> new ResourceNotFoundException("Person " + firstName + " " + lastName + " was " +
                                                                                         "not found."));
            return new ResponseEntity<>(List.of(person), HttpStatus.OK);
        } else {
            log.error("Person Controller: Bad request for GET method.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Create - Add a new person
     *
     * @param person
     *         An object person
     *
     * @return HTTP response with: - created (201) when save performed successfully - body: saved person
     *
     * @throws Exception
     *         when an error occurred while creating a new person
     */
    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) throws Exception {
        log.debug("Person Controller: POST/person");
        Person savedPerson = personService.savePerson(person);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }


    /**
     * Update - Update an existing person
     *
     * @param person
     *         - The person object updated
     *
     * @return HTTP response with: - ok (200) when save performed successfully - not found (404) when the person does
     *         not exist - body: updated Person object
     */
    @PutMapping
    public ResponseEntity<Person> updatePerson(@RequestBody Person person) throws Exception {
        log.debug("Person Controller: PUT/person");
        return new ResponseEntity<>(personService.updatePerson(person), HttpStatus.OK);
    }


    /**
     * Delete - Delete an person
     *
     * @param firstName
     *         First name of person to find
     * @param lastName
     *         Last name of person to find
     *
     * @return HTTP response with: - no content (204) when person was successfully deleted - empty body
     */
    @DeleteMapping
    public ResponseEntity<String> deletePerson(@RequestParam String firstName,
                                               @RequestParam String lastName) throws Exception {
        log.debug("Person Controller: DELETE/person?firstName={}&lastName={}", firstName, lastName);
        personService.deletePerson(firstName, lastName);
        return ResponseEntity.noContent().build();
    }
}
