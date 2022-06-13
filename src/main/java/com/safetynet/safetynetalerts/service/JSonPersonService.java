package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.ChildAlertViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.ChildViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FirePersonViewModel;
import com.safetynet.safetynetalerts.repository.MedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;
import com.safetynet.safetynetalerts.utils.AgeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Data
@Service
@Slf4j
public class JSonPersonService implements PersonService {

    /**
     * Instance of PersonRepository.
     */
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private AgeUtil ageUtil;

    public JSonPersonService(PersonRepository PersonRepository) {
        this.personRepository = PersonRepository;
    }

    /**
     * Get person.
     *
     * @param firstName
     *         Person's first name
     * @param lastName
     *         Person's last name
     *
     * @return Person a person if not empty
     */
    @Override
    public Optional<Person> getPersonByName(final String firstName, final String lastName) {
        return personRepository.findByName(firstName, lastName);
    }

    /**
     * Get the list of all persons.
     *
     * @return an iterable of Persons
     */
    @Override
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    /**
     * Delete person with given name.
     *
     * @param firstName
     *         First name of person to delete
     * @param lastName
     *         Last name of person to delete
     */
    @Override
    public void deletePerson(final String firstName, final String lastName) throws Exception {
        personRepository.deleteByName(firstName, lastName);
    }

    /**
     * Update person with given name.
     *
     * @param person
     *         Person to update
     */
    @Override
    public Person updatePerson(final Person person) throws Exception {
        String firstName = person.getFirstName();
        String lastName  = person.getLastName();

        Optional<Person> personInDataSource = personRepository.findByName(firstName, lastName);
        if (personInDataSource.isEmpty()) {
            String notFoundMessage = "Person " + firstName + " " + lastName + " does not exist.";
            log.error(notFoundMessage);
            throw new ResourceNotFoundException(notFoundMessage);
        } else {
            return savePerson(person);
        }
    }

    /**
     * Save person.
     *
     * @param person
     *         Person to save
     *
     * @return Person
     */
    @Override
    public Person savePerson(final Person person) throws Exception {
        String           firstName = person.getFirstName();
        String           lastName  = person.getLastName();
        Optional<Person> duplicate = personRepository.findByName(firstName, lastName);
        if (duplicate.isPresent()) {
            personRepository.deleteByName(firstName, lastName);
        }
        return personRepository.save(person);
    }

    public Map<String, Integer> getNbChildrenAndNbAdults(List<Person> list) {
        int                  nbChildren    = 0;
        int                  nbAdults      = 0;
        Map<String, Integer> familyMembers = new HashMap<>();
        for (Person person : list) {
            String    firstName = person.getFirstName();
            String    lastName  = person.getLastName();
            LocalDate birthdate = medicalRecordRepository.getBirthDateByName(firstName, lastName);
            if (ageUtil.isChild(birthdate)) {
                nbChildren++;
            } else {
                nbAdults++;
            }
        }
        familyMembers.put("nb_adults", nbAdults);
        familyMembers.put("nb_children", nbChildren);
        return familyMembers;
    }

    /**
     * Gets children living at a given address.
     *
     * @param address
     *         Address where to find children.
     *
     * @return a list of children and other household members living at given address
     */
    public ChildAlertViewModel getChildAlert(String address) {
        ChildAlertViewModel       result                   = new ChildAlertViewModel();
        List<ChildViewModel>      childrenAtAddress        = new ArrayList<>();
        List<FirePersonViewModel> peopleAtAddressViewModel = new ArrayList<>();
        List<Person>              peopleAtAddress          = personRepository.findByAddress(address);

        for (Person person : peopleAtAddress) {
            String    firstName = person.getFirstName();
            String    lastName  = person.getLastName();
            LocalDate birthdate = medicalRecordRepository.getBirthDateByName(firstName, lastName);
            int       age       = ageUtil.calculateAge(birthdate);
            boolean   isChild   = ageUtil.isChild(birthdate);

            if (isChild) {
                childrenAtAddress.add(new ChildViewModel(firstName, lastName, age));
            } else {
                peopleAtAddressViewModel.add(new FirePersonViewModel(person.getLastName(),
                                                                     person.getPhone(),
                                                                     age,
                                                                     medicalRecordRepository.getMedicationsByName(firstName, lastName),
                                                                     medicalRecordRepository.getAllergiesByName(firstName, lastName)));
            }
        }
        result.setChildren(childrenAtAddress);
        result.setOtherHouseholdMembers(peopleAtAddressViewModel);
        return result;
    }
}
