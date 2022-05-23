package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.JSonPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private JSonPersonService jSonPersonService;


    @GetMapping
    public List<Person> getPersons() {
        return jSonPersonService.getPersons();
    }

    @GetMapping("/{firstName}/{lastName}")
    public Person getPerson(@PathVariable String firstName, @PathVariable String lastName) {
        return jSonPersonService.getPersonByName(firstName, lastName).
                                orElseThrow(() -> new ResourceNotFoundException("Person " + firstName + " " + lastName +
                                                                                " was not found."));
    }


    @PostMapping
    public Person createPerson(@RequestBody Person person) throws Exception {
        return jSonPersonService.savePerson(person);
    }


    @PutMapping
    public Person updatePerson(@RequestBody Person person) throws Exception {
        return jSonPersonService.updatePerson(person);
    }


    @DeleteMapping("/{firstName}/{lastName}")
    public void deletePerson(@PathVariable String firstName, @PathVariable String lastName) throws Exception {
        jSonPersonService.deletePerson(firstName, lastName);
    }
}
