package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.IPersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class PersonService implements IPersonService {

    /**
     * Instance of IPersonRepository.
     */
    @Autowired
    private IPersonRepository IPersonRepository;

    public PersonService(IPersonRepository IPersonRepository) {
        this.IPersonRepository = IPersonRepository;
    }

    /**
     * Get person.
     *
     * @param firstName Person's first name
     * @param lastName  Person's last name
     *
     * @return Person a person if not empty
     */
    @Override
    public Optional<Person> getPersonByName(final String firstName, final String lastName) {
        return IPersonRepository.findByName(firstName, lastName);
    }

    /**
     * Get the list of all persons.
     *
     * @return an iterable of Persons
     */
    @Override
    public Iterable<Person> getPersons() {
        return IPersonRepository.findAll();
    }

    /**
     * Delete person with given name.
     *
     * @param firstName First name of person to delete
     * @param lastName  Last name of person to delete
     */
    @Override
    public void deletePerson(final String firstName, final String lastName) throws Exception {
        IPersonRepository.deleteByName(firstName, lastName);
    }

    /**
     * Update person with given name.
     *
     * @param person Person to update
     */
    @Override
    public Person updatePerson(final Person person) throws Exception {
        return IPersonRepository.update(person);
    }

    /**
     * Save person.
     *
     * @param person Person to save
     *
     * @return Person
     */
    @Override
    public Person savePerson(final Person person) throws Exception {
        return IPersonRepository.save(person);
    }
}
