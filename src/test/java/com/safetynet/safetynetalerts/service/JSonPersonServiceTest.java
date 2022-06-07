package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.ChildAlertViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.PersonAtAddressViewModel;
import com.safetynet.safetynetalerts.repository.JSonMedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.JSonPersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class JSonPersonServiceTest {
    /**
     * Class under test
     */
    @Autowired
    private JSonPersonService jSonPersonService;

    @MockBean
    private JSonPersonRepository jSonPersonRepository;

    @MockBean
    private JSonMedicalRecordRepository jSonMedicalRecordRepository;

    @Test
    void update_shouldReturn_updatedPerson_whenPersonExists() throws Exception {
        // GIVEN existing  person John Boyd with different address and different phone number
        String firstName  = "John";
        String lastName   = "Boyd";
        String newAddress = "112 Steppes Pl";
        String newPhone   = "841-874-9888";
        String newCity    = "Figeac";
        int    newZip     = 46100;
        String newEmail   = "johnboyd@email.com";

        Person johnBoyd = new Person(firstName,
                                     lastName,
                                     newAddress,
                                     newCity,
                                     newZip,
                                     newPhone,
                                     newEmail);
        when(jSonPersonRepository.findByName(firstName, lastName)).thenReturn(Optional.of(johnBoyd));
        when(jSonPersonRepository.save(any(Person.class))).thenReturn(johnBoyd);

        // WHEN calling updatePerson()
        Person updatedPerson = jSonPersonService.updatePerson(johnBoyd);

        //THEN
        assertThat(updatedPerson.getAddress()).isEqualTo(newAddress);
        assertThat(updatedPerson.getCity()).isEqualTo(newCity);
        assertThat(updatedPerson.getZip()).isEqualTo(newZip);
        assertThat(updatedPerson.getPhone()).isEqualTo(newPhone);
        assertThat(updatedPerson.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void update_shouldNot_AddDuplicates() throws Exception {
        // GIVEN existing  person John Boyd with different address and different phone number
        String firstName  = "John";
        String lastName   = "Boyd";
        String newAddress = "112 Steppes Pl";
        String newPhone   = "841-874-9888";
        String newCity    = "Figeac";
        int    newZip     = 46100;
        String newEmail   = "johnboyd@email.com";

        Person johnBoyd = new Person(firstName,
                                     lastName,
                                     newAddress,
                                     newCity,
                                     newZip,
                                     newPhone,
                                     newEmail);
        when(jSonPersonRepository.findByName(firstName, lastName)).thenReturn(Optional.of(johnBoyd));
        when(jSonPersonRepository.save(any(Person.class))).thenReturn(johnBoyd);

        List<Person> peopleBeforeUpdate   = jSonPersonService.getPersons();
        int          nbPeopleBeforeUpdate = peopleBeforeUpdate.size();

        // WHEN calling update()
        Person updatedPerson = jSonPersonService.updatePerson(johnBoyd);

        //THEN
        List<Person> peopleAfterUpdate = jSonPersonService.getPersons();
        assertThat(peopleAfterUpdate.size()).isEqualTo(nbPeopleBeforeUpdate);
    }

    @Test
    void update_shouldThrowException_whenPersonDoesNotExist() throws ResourceNotFoundException {
        //GIVEN a person who does not exist in the test data source
        String firstName    = "Brian";
        String lastName     = "Stelzer";
        Person brianStelzer = new Person();
        brianStelzer.setFirstName(firstName);
        brianStelzer.setLastName(lastName);
        when(jSonPersonRepository.findByName(firstName, lastName)).thenReturn(Optional.empty());
        Optional<Person> nonExistingPerson = jSonPersonService.getPersonByName(firstName, lastName);
        // make sure the person does not exist before running check
        assertThat(nonExistingPerson).isEmpty();

        // WHEN calling update()
        // THEN there must be an exception thrown
        assertThrows(ResourceNotFoundException.class, () -> jSonPersonService.updatePerson(brianStelzer));
    }

    @Test
    void getChildAlert_shouldReturn_theRightObject() {
        String       address     = "1509 Culver St";
        List<String> medications = List.of("aznol:350mg", "hydrapermazol:100mg");
        List<String> allergies   = List.of("nillacilan");
        Person person1 = new Person("John",
                                    "Boyd",
                                    "1509 Culver St",
                                    "Culver",
                                    97451,
                                    "841-874-6512",
                                    "jaboyd@email.com");
        Person person2 = new Person("Felicia",
                                    "Boyd",
                                    "1509 Culver St",
                                    "Culver",
                                    97451,
                                    "841-874-6544",
                                    "jaboyd@email.com");
        PersonAtAddressViewModel person1AtAddress = new PersonAtAddressViewModel("Boyd",
                                                                                 "841-874-6512",
                                                                                 38,
                                                                                 medications,
                                                                                 allergies);
        PersonAtAddressViewModel person2AtAddress = new PersonAtAddressViewModel("Boyd",
                                                                                 "841-874-6544",
                                                                                 38,
                                                                                 medications,
                                                                                 allergies);
        List<Person>                   listPeople                   = List.of(person1, person2);
        List<PersonAtAddressViewModel> listPersonAtAddressViewModel = List.of(person1AtAddress, person2AtAddress);
        when(jSonPersonRepository.findByAddress(address)).thenReturn(listPeople);
        when(jSonMedicalRecordRepository.getBirthDateByName(any(String.class), any(String.class))).thenReturn(LocalDate.of(1984, 10, 12));
        when(jSonMedicalRecordRepository.getMedicationsByName(any(String.class), any(String.class))).thenReturn(medications);
        when(jSonMedicalRecordRepository.getAllergiesByName(any(String.class), any(String.class))).thenReturn(allergies);

        ChildAlertViewModel result = jSonPersonService.getChildAlert(address);

        assertThat(result.getChildren()).isEqualTo(Collections.emptyList());
        assertThat(result.getOtherHouseholdMembers()).isEqualTo(listPersonAtAddressViewModel);
    }
}