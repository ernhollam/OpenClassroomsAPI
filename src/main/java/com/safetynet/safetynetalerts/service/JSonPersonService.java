package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.*;
import com.safetynet.safetynetalerts.repository.FirestationRepository;
import com.safetynet.safetynetalerts.repository.MedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;
import com.safetynet.safetynetalerts.utils.AgeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


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
    private FirestationRepository firestationRepository;

    @Autowired
    private AgeUtil ageUtil;

    public JSonPersonService(PersonRepository PersonRepository) {
        this.personRepository = PersonRepository;
    }

    /**
     * Saves person.
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

    /**
     * Finds person with specified names.
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
     * Returns the list of all persons.
     *
     * @return an iterable of Persons
     */
    @Override
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    /**
     * Updates person with given name.
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
            log.info("Updated person {}.", person);
            return savePerson(person);
        }
    }

    /**
     * Deletes person with given name.
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

        log.debug("There are {} children and {} adults in the following list:\n{}", nbChildren, nbAdults, list);

        return familyMembers;
    }


    /**
     * Returns list of children living at a given address.
     *
     * @param address
     *         Address where to find children.
     *
     * @return a list of children and other household members living at given address
     */
    @Override
    public ChildAlertViewModel getChildAlert(String address) {
        ChildAlertViewModel       result            = new ChildAlertViewModel();
        List<ChildViewModel>      childrenAtAddress = new ArrayList<>();
        List<FirePersonViewModel> firePeople        = new ArrayList<>();
        List<Person>              peopleAtAddress   = personRepository.findByAddress(address);

        for (Person person : peopleAtAddress) {
            String    firstName = person.getFirstName();
            String    lastName  = person.getLastName();
            LocalDate birthdate = medicalRecordRepository.getBirthDateByName(firstName, lastName);
            int       age       = ageUtil.calculateAge(birthdate);
            boolean   isChild   = ageUtil.isChild(birthdate);

            if (isChild) {
                childrenAtAddress.add(new ChildViewModel(firstName, lastName, age));
            } else {
                firePeople.add(personToFirePerson(person));
            }
        }
        result.setChildren(childrenAtAddress);
        result.setOtherHouseholdMembers(firePeople);

        log.debug("The following children are living at address {}:\n{}\nAdults living in the household are:\n{}",
                  address, childrenAtAddress, firePeople);

        return result;
    }

    /**
     * Returns a list of people living at a given address, as well as the station number covering the house.
     *
     * @param address
     *         Address where to find people.
     *
     * @return a list of people living at address and the fire station which covers the address.
     */
    @Override
    public FireViewModel getFirePeople(String address) {
        int                   stationNumber;
        Optional<Firestation> firestation = firestationRepository.findByAddress(address);

        if (firestation.isEmpty()) {
            String errorMessage = "There is no fire station covering this address: " + address + ".";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        } else {
            stationNumber = firestation.get().getStation();
        }

        List<Person>             peopleAtAddress = personRepository.findByAddress(address);
        Set<FirePersonViewModel> firePeople      = new HashSet<>(peopleAtAddress.size());
        for (Person person : peopleAtAddress) {
            firePeople.add(personToFirePerson(person));
        }

        log.debug("The following people are living at address {}:\n{}\nFire station covering them is station " +
                  "number {}.", address, firePeople, stationNumber);

        return new FireViewModel(firePeople, stationNumber);
    }

    /**
     * Returns information about given person and people with the same name.
     *
     * @param firstName
     *         Person's first name.
     * @param lastName
     *         Person's last name
     *
     * @return Information about the person and a list of people with same name.
     */
    @Override
    public PersonInfoViewModel getPersonInfo(String firstName, String lastName) {
        Optional<Person> person = personRepository.findByName(firstName, lastName);

        PersonInfoViewModel personInfoViewModel = new PersonInfoViewModel();

        if (person.isEmpty()) {
            String errorMessage = "Person " + firstName + " " + lastName + " does not exist.";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        } else {
            Person foundPerson    = person.get();
            String foundFirstName = foundPerson.getFirstName();
            String foundLastName  = foundPerson.getLastName();

            List<Person> allPeople = personRepository.findAll();
            List<Person> peopleWithSameName = allPeople.stream()
                                                       .filter(p -> p.getLastName().equalsIgnoreCase(foundLastName) && !(p.getFirstName().equalsIgnoreCase(foundFirstName)))
                                                       .collect(Collectors.toList());
            personInfoViewModel.setPeopleWithSameName(peopleWithSameName);

            personInfoViewModel.setLastName(foundLastName);
            personInfoViewModel.setAddress(foundPerson.getAddress());
            personInfoViewModel.setAge(ageUtil.calculateAge(medicalRecordRepository.getBirthDateByName(foundFirstName,
                                                                                                       foundLastName)));
            personInfoViewModel.setEmail(foundPerson.getEmail());
            personInfoViewModel.setMedications(medicalRecordRepository.getMedicationsByName(foundFirstName,
                                                                                            foundLastName));
            personInfoViewModel.setAllergies(medicalRecordRepository.getAllergiesByName(foundFirstName, foundLastName));

            log.debug("Information about {} {}:\n{}", firstName, lastName, personInfoViewModel);

        }

        return personInfoViewModel;
    }

    /**
     * Returns a list of all email addresses of people living in a city.
     */
    @Override
    public Set<String> getCommunityEmail(String city) {
        List<Person> peopleInCity = personRepository.findByCity(city);
        Set<String> emails = peopleInCity.stream()
                                         .map(Person :: getEmail).collect(Collectors.toSet());
        log.debug("List of email addresses of people living in {}:\n{}", city, emails);
        return emails;
    }

    /**
     * Transforms a Person into a FirePerson
     *
     * @param person
     *         Person
     *
     * @return FirePerson
     */
    public FirePersonViewModel personToFirePerson(Person person) {
        String firstName = person.getFirstName();
        String lastName  = person.getLastName();
        return new FirePersonViewModel(lastName,
                                       person.getPhone(),
                                       ageUtil.calculateAge(medicalRecordRepository.getBirthDateByName(firstName,
                                                                                                       lastName)),
                                       medicalRecordRepository.getMedicationsByName(firstName, lastName),
                                       medicalRecordRepository.getAllergiesByName(firstName, lastName));
    }
}
