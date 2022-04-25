package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

@Repository
public class JSonPersonRepository implements PersonRepository {

    @Autowired
    private static JSonReader jsonReader;

    /**
     * Save person into JSon file.
     *
     * @param person Person to save
     *
     * @return person saved
     */
    @Override
    public Person save(Person person) {
        return null;
    }

    /**
     * Get list of all persons in JSON file.
     *
     * @return list of persons.
     */
    @Override
    public Iterable<Person> findAll() {
        return jsonReader.getPeopleFromJsonFile();
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
        Iterable<Person> people      = jsonReader.getPeopleFromJsonFile();

        for (Person person : people) {
            if (person.getFirstName().equalsIgnoreCase(firstName) && person.getLastName().equalsIgnoreCase(lastName)) {
                foundPerson = Optional.of(person);
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
    public void deleteByName(String firstName, String lastName) throws IOException {
        Optional<Person> personToDelete = findByName(firstName, lastName);
        Iterable<Person> people = jsonReader.getPeopleFromJsonFile();

        if (personToDelete.isPresent()) {
            Iterator<Person> iterator = people.iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
                if (person.equals(personToDelete.get())) {
                    iterator.remove();
                }
            }
            //mapper.writeValue(jsonFile, people);
        }
    }
}
