package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class PersonController {

    @Autowired
    private static PersonService personService;

    /**
     * Read - Get all persons from date file
     * @return - An Iterable object of Person fulfilled
     */
    @GetMapping("/persons")
    public Iterable<Person> getPersons() {
        return personService.getPersons();
    }

    /**
     * Create - Add a new person
     * @param person An object person
     * @return The person object saved
     */
    @PostMapping("/person")
    public Person createPerson(@RequestBody Person person) {
        return personService.savePerson(person);
    }

    /**
     * Read - Get one person
     * @param id The id of the person
     * @return A Person object fulfilled
     */
    @GetMapping("/person/{id}")
    public Person getPerson(@PathVariable("id") final Long id) {
        Optional<Person> person = personService.getPerson(id);
        return person.orElse(null);
    }

    /**
     * Update - Update an existing person
     * @param id - The id of the person to update
     * @param person - The person object updated
     * @return optional Person
     */
    @PutMapping("/person/{id}")
    public Person updatePerson(@PathVariable("id") final Long id, @RequestBody Person person) {
        Optional<Person> personToUpdate = personService.getPerson(id);
        if(personToUpdate.isPresent()) {
            Person personBeforeUpdate = personToUpdate.get();

            String address = person.getAddress();
            if(address != null) {
                personBeforeUpdate.setAddress(address);
            }

            String city = person.getCity();
            if(city != null) {
                personBeforeUpdate.setCity(city);
            }

            int zip = person.getZip();
            if(zip != 0) {
                personBeforeUpdate.setZip(zip);
            }

            String phone = person.getPhone();
            if(phone != null) {
                personBeforeUpdate.setPhone(phone);
            }

            String mail = person.getEmail();
            if(mail != null) {
                personBeforeUpdate.setEmail(mail);
            }

            personService.savePerson(personBeforeUpdate);
            return personBeforeUpdate;

        } else {
            return null;
        }
    }


    /**
     * Delete - Delete an person
     * @param id - The id of the person to delete
     */
    @DeleteMapping("/person/{id}")
    public void deletePerson(@PathVariable("id") final Long id) {
        personService.deletePerson(id);
    }
}
