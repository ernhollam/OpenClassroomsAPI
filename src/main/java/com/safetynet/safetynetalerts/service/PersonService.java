package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class PersonService {

    /**
     * Instance of PersonRepository.
     */
    @Autowired
    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Get person.
     *
     * @param firstName Person's first name
     * @param lastName  Person's last name
     *
     * @return Person a person if not empty
     */
    public Optional<Person> getPersonByName(final String firstName, final String lastName) {
        return personRepository.findByName(firstName, lastName);
    }

    /**
     * Get the list of all persons.
     *
     * @return an iterable of Persons
     */
    public Iterable<Person> getPersons() {
        return personRepository.findAll();
    }

    /**
     * Delete person with given name.
     *
     * @param firstName First name of person to delete
     * @param lastName  Last name of person to delete
     */
    public void deletePerson(final String firstName, final String lastName) throws Exception {
        personRepository.deleteByName(firstName, lastName);
    }

    /**
     * Delete person with given name.
     *
     * @param person Person to update
     */
    public Person updatePerson(final Person person) throws Exception {
        return personRepository.update(person);
    }

    /**
     * Save person.
     *
     * @param person Person to save
     *
     * @return Person
     */
    public Person savePerson(final Person person) throws Exception {
        return personRepository.save(person);
    }
}
