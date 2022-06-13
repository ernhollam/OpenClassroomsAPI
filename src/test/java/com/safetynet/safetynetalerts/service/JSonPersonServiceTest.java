package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.ChildAlertViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.ChildViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FirePersonViewModel;
import com.safetynet.safetynetalerts.repository.JSonMedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.JSonPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final static LocalDate LOCAL_DATE_NOW = LocalDate.of(2022, 6, 13);

    @MockBean
    private Clock clock;

    @BeforeEach
    public void init() {
        // configure a fixed clock to have fixe LocalDate.now()
        Clock fixedClock = Clock.fixed(LOCAL_DATE_NOW.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                                       ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

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
    void getNbChildrenAndNbAdults_shouldFind_oneChildAndTwoAdults() {
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
        Person person3 = new Person("Tenley",
                                    "Boyd",
                                    "1509 Culver St",
                                    "Culver",
                                    97451,
                                    "841-874-6512",
                                    "tenz@email.com");
        List<Person> listPeople = List.of(person1, person2, person3);
        when(jSonMedicalRecordRepository.getBirthDateByName(person1.getFirstName(), person1.getLastName()))
                .thenReturn(LocalDate.of(1984, 10, 12));
        when(jSonMedicalRecordRepository.getBirthDateByName(person2.getFirstName(), person2.getLastName()))
                .thenReturn(LocalDate.of(1986, 1, 8));
        when(jSonMedicalRecordRepository.getBirthDateByName(person3.getFirstName(), person3.getLastName()))
                .thenReturn(LocalDate.of(2012, 2, 18));

        Map<String, Integer> familyMembers = jSonPersonService.getNbChildrenAndNbAdults(listPeople);

        assertThat(familyMembers.get("nb_adults")).isEqualTo(2);
        assertThat(familyMembers.get("nb_children")).isEqualTo(1);
    }

    @Test
    void getNbChildrenAndNbAdults_shouldFind_oneChildAndNoAdults() {
        List<Person> listPeople = List.of(new Person("Tessa",
                                                     "Carman",
                                                     "834 Binoc Ave",
                                                     "Culver",
                                                     97451,
                                                     "841-874-6512",
                                                     "tenz@email.com"));
        when(jSonMedicalRecordRepository.getBirthDateByName(any(String.class), any(String.class))).thenReturn(LocalDate.of(2012, 2, 18));

        Map<String, Integer> familyMembers = jSonPersonService.getNbChildrenAndNbAdults(listPeople);

        assertThat(familyMembers.get("nb_adults")).isEqualTo(0);
        assertThat(familyMembers.get("nb_children")).isEqualTo(1);
    }

    @Test
    void getNbChildrenAndNbAdults_shouldFind_noChildAndOneAdult() {
        List<Person> listPeople = List.of(new Person("Jonanathan",
                                                     "Marrack",
                                                     "29 15th St",
                                                     "Culver",
                                                     97451,
                                                     "841-874-6513",
                                                     "drk@email.com"));
        when(jSonMedicalRecordRepository.getBirthDateByName(any(String.class), any(String.class))).thenReturn(LocalDate.of(1989, 1, 3));

        Map<String, Integer> familyMembers = jSonPersonService.getNbChildrenAndNbAdults(listPeople);

        assertThat(familyMembers.get("nb_adults")).isEqualTo(1);
        assertThat(familyMembers.get("nb_children")).isEqualTo(0);
    }

    @Test
    void getChildAlert_shouldReturn_OneChildAndTwoAdults() {
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
        Person person3 = new Person("Tenley",
                                    "Boyd",
                                    "1509 Culver St",
                                    "Culver",
                                    97451,
                                    "841-874-6512",
                                    "tenz@email.com");
        FirePersonViewModel expectedFirePerson1 = new FirePersonViewModel("Boyd",
                                                                          "841-874-6512",
                                                                          38,
                                                                          medications,
                                                                          allergies);
        FirePersonViewModel expectedFirePerson2 = new FirePersonViewModel("Boyd",
                                                                          "841-874-6544",
                                                                          36,
                                                                          medications,
                                                                          allergies);

        ChildViewModel expectedFirePerson3 = new ChildViewModel("Tenley",
                                                                "Boyd",
                                                                10);
        List<Person>              listPeople                  = List.of(person1, person2, person3);
        List<FirePersonViewModel> expectedAdultFirePersonList = List.of(expectedFirePerson1, expectedFirePerson2);
        when(jSonPersonRepository.findByAddress(address)).thenReturn(listPeople);
        when(jSonMedicalRecordRepository.getBirthDateByName(person1.getFirstName(), person1.getLastName()))
                .thenReturn(LocalDate.of(1984, 10, 12));
        when(jSonMedicalRecordRepository.getBirthDateByName(person2.getFirstName(), person2.getLastName()))
                .thenReturn(LocalDate.of(1986, 1, 8));
        when(jSonMedicalRecordRepository.getBirthDateByName(person3.getFirstName(), person3.getLastName()))
                .thenReturn(LocalDate.of(2012, 2, 18));
        when(jSonMedicalRecordRepository.getMedicationsByName(any(String.class), any(String.class))).thenReturn(medications);
        when(jSonMedicalRecordRepository.getAllergiesByName(any(String.class), any(String.class))).thenReturn(allergies);

        ChildAlertViewModel result = jSonPersonService.getChildAlert(address);

        assertThat(result.getChildren()).isEqualTo(List.of(expectedFirePerson3));
        assertThat(result.getOtherHouseholdMembers()).isEqualTo(expectedAdultFirePersonList);
    }

    @Test
    void getChildAlert_shouldReturn_oneChildAndNoAdults() {
        String       address     = "1509 Culver St";
        List<String> medications = Collections.emptyList();
        List<String> allergies   = Collections.emptyList();
        List<Person> listPeople = List.of(new Person("Tessa",
                                                     "Carman",
                                                     "834 Binoc Ave",
                                                     "Culver",
                                                     97451,
                                                     "841-874-6512",
                                                     "tenz@email.com"));
        List<ChildViewModel> child = List.of(new ChildViewModel("Tessa", "Carman", 10));
        when(jSonPersonRepository.findByAddress(address)).thenReturn(listPeople);
        when(jSonMedicalRecordRepository.getBirthDateByName(any(String.class), any(String.class))).thenReturn(LocalDate.of(2012, 2, 18));
        when(jSonMedicalRecordRepository.getMedicationsByName(any(String.class), any(String.class))).thenReturn(medications);
        when(jSonMedicalRecordRepository.getAllergiesByName(any(String.class), any(String.class))).thenReturn(allergies);

        ChildAlertViewModel result = jSonPersonService.getChildAlert(address);

        assertThat(result.getOtherHouseholdMembers()).isEqualTo(Collections.emptyList());
        assertThat(result.getChildren()).isEqualTo(child);
    }

    @Test
    void getChildAlert_shouldReturn_noChildAndOneAdult() {
        String       address     = "1509 Culver St";
        List<String> medications = List.of("aznol:350mg", "hydrapermazol:100mg");
        List<String> allergies   = List.of("nillacilan");
        List<Person> listPeople = List.of(new Person("Jonanathan",
                                                     "Marrack",
                                                     "29 15th St",
                                                     "Culver",
                                                     97451,
                                                     "841-874-6513",
                                                     "drk@email.com"));

        List<FirePersonViewModel> listFirePersonViewModel = List.of(new FirePersonViewModel("Marrack",
                                                                                            "841-874-6513",
                                                                                            33,
                                                                                            medications,
                                                                                            allergies));
        when(jSonPersonRepository.findByAddress(address)).thenReturn(listPeople);
        when(jSonMedicalRecordRepository.getBirthDateByName(any(String.class), any(String.class))).thenReturn(LocalDate.of(1989, 1, 3));
        when(jSonMedicalRecordRepository.getMedicationsByName(any(String.class), any(String.class))).thenReturn(medications);
        when(jSonMedicalRecordRepository.getAllergiesByName(any(String.class), any(String.class))).thenReturn(allergies);

        ChildAlertViewModel result = jSonPersonService.getChildAlert(address);

        assertThat(result.getChildren()).isEqualTo(Collections.emptyList());
        assertThat(result.getOtherHouseholdMembers()).isEqualTo(listFirePersonViewModel);
    }
}