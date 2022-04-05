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

    /**
     * Get person.
     *
     * @param id Person's id
     *
     * @return Person a person if not empty
     */
    public Optional<Person> getPerson(final Long id) {
        return personRepository.findById(id);
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
     * Delete person with given id.
     *
     * @param id ID of person to delete
     */
    public void deletePerson(final Long id) {
        personRepository.deleteById(id);
    }

    /**
     * Save person.
     *
     * @param person Person to save
     *
     * @return Person
     */
    public Person savePerson(final Person person) {
        return personRepository.save(person);
    }
}
