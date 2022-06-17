package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.FirePersonViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationPersonViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FloodViewModel;
import com.safetynet.safetynetalerts.repository.JSonFirestationRepository;
import com.safetynet.safetynetalerts.repository.JSonMedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.JSonPersonRepository;
import com.safetynet.safetynetalerts.utils.AgeUtilTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSonFirestationServiceTest {
    /**
     * Class under test.
     */
    @Autowired
    private JSonFirestationService jSonFirestationService;

    @MockBean
    private JSonFirestationRepository jSonFirestationRepository;

    @MockBean
    private JSonPersonRepository jSonPersonRepository;

    @MockBean
    private JSonMedicalRecordRepository jSonMedicalRecordRepository;

    @MockBean
    private Clock clock;

    private Person       person1;
    private Person       person2;
    private Person       person3;
    private List<Person> people;

    @BeforeAll
    void setUp() {

        person1 = new Person("John",
                             "Boyd",
                             "1509 Culver St",
                             "Culver",
                             97451,
                             "841-874-6512",
                             "jaboyd@email.com");
        person2 = new Person("Felicia",
                             "Boyd",
                             "1509 Culver St",
                             "Culver",
                             97451,
                             "841-874-6544",
                             "jaboyd@email.com");
        person3 = new Person("Tenley",
                             "Boyd",
                             "1509 Culver St",
                             "Culver",
                             97451,
                             "841-874-6512",
                             "tenz@email.com");

        people = List.of(person1, person2, person3);
    }

    @BeforeEach
    void setUpBirthdates() {

        LocalDate birthdate1 = LocalDate.of(1984, 3, 6);
        LocalDate birthdate2 = LocalDate.of(1986, 1, 8);
        LocalDate birthdate3 = LocalDate.of(2012, 2, 18);


        when(jSonMedicalRecordRepository.getBirthDateByName(person1.getFirstName(), person1.getLastName()))
                .thenReturn(birthdate1);
        when(jSonMedicalRecordRepository.getBirthDateByName(person2.getFirstName(), person2.getLastName()))
                .thenReturn(birthdate2);
        when(jSonMedicalRecordRepository.getBirthDateByName(person3.getFirstName(), person3.getLastName()))
                .thenReturn(birthdate3);


        // configure a fixed clock to have fixe LocalDate.now()
        Clock fixedClock = Clock.fixed(AgeUtilTest.LOCAL_DATE_NOW.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                                       ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void update_shouldReturn_updatedFirestation_whenExists() throws Exception {
        // GIVEN existing firestation with new address
        int         stationNumber = 3;
        String      address       = "834 Binoc Ave";
        Firestation firestation   = new Firestation(address, stationNumber);
        when(jSonFirestationRepository.findByAddress(any(String.class))).thenReturn(Optional.of(firestation));
        when(jSonFirestationRepository.save(any(Firestation.class))).thenReturn(firestation);
        // WHEN calling update()
        Firestation updatedFirestation = jSonFirestationService.updateFirestation(firestation);

        //THEN
        assertThat(updatedFirestation.getStation()).isEqualTo(stationNumber);
        assertThat(updatedFirestation.getAddress()).isEqualTo(address);
    }

    @Test
    void update_shouldNot_AddDuplicates() throws Exception {
        // GIVEN existing firestation with new address
        int         stationNumber = 3;
        String      address       = "834 Binoc Ave";
        Firestation firestation   = new Firestation(address, stationNumber);
        when(jSonFirestationRepository.findByAddress(any(String.class))).thenReturn(Optional.of(firestation));
        when(jSonFirestationRepository.save(any(Firestation.class))).thenReturn(firestation);

        List<Firestation> firestationsBeforeUpdate   = jSonFirestationService.getFirestations();
        int               nbFirestationsBeforeUpdate = firestationsBeforeUpdate.size();

        // WHEN calling update()
        Firestation updatedFirestation = jSonFirestationService.updateFirestation(firestation);

        //THEN
        List<Firestation> firestationsAfterUpdate = jSonFirestationService.getFirestations();
        assertThat(firestationsAfterUpdate.size()).isEqualTo(nbFirestationsBeforeUpdate);
    }

    @Test
    void update_shouldThrowException_whenDoesNotExist() throws ResourceNotFoundException {
        //GIVEN a firestation which does not exist
        Firestation firestation = new Firestation("dummy address", 1);

        // WHEN calling update()
        // THEN there must be an exception thrown
        assertThrows(ResourceNotFoundException.class, () -> jSonFirestationService.updateFirestation(firestation));
    }

    @Test
    void getPeopleCoveredByStation() {
        int               stationNumber       = 2;
        List<Firestation> coveredFirestations = List.of(new Firestation("1509 Culver St", stationNumber));
        when(jSonFirestationRepository.findByStationNumber(stationNumber)).thenReturn(coveredFirestations);

        when(jSonPersonRepository.findByAddress(any(String.class))).thenReturn(people);


        List<FirestationPersonViewModel> expectedFirePersonList = List.of(
                new FirestationPersonViewModel(person1.getFirstName(), person1.getLastName(), person1.getAddress(),
                                               person1.getPhone()),
                new FirestationPersonViewModel(person2.getFirstName(), person2.getLastName(), person2.getAddress(),
                                               person2.getPhone()),
                new FirestationPersonViewModel(person3.getFirstName(), person3.getLastName(), person3.getAddress(),
                                               person3.getPhone()));


        FirestationViewModel result = jSonFirestationService.getPeopleCoveredByStation(stationNumber);

        assertThat(result.getPeople()).isEqualTo(expectedFirePersonList);
        assertThat(result.getNbAdults()).isEqualTo(2);
        assertThat(result.getNbChildren()).isEqualTo(1);
    }

    // TODO deleteFirestation() test
    @Test
    void deleteFirestation() {
    }


    @Test
    void getPhoneAlert_shouldReturn_aListOfPhoneNumbers() {
        int               stationNumber = 1;
        String            address       = "test address";
        List<Firestation> firestations  = List.of(new Firestation(address, stationNumber));
        when(jSonFirestationRepository.findByStationNumber(stationNumber)).thenReturn(firestations);
        when(jSonPersonRepository.findByAddress(address)).thenReturn(people);
        when(jSonMedicalRecordRepository.getBirthDateByName(any(String.class), any(String.class))).thenReturn(LocalDate.of(1998, 7, 12));

        Set<String> phoneNumbers         = jSonFirestationService.getPhoneAlert(stationNumber);
        Set<String> expectedPhoneNumbers = Set.of("841-874-6512", "841-874-6544");

        assertThat(phoneNumbers).isEqualTo(expectedPhoneNumbers);
        assertThat(phoneNumbers.size()).isEqualTo(2);

    }

    @Test
    void getCoveredHouseholds_shouldReturn_firePerson() {
        int    stationNumber = 17;
        String address1      = "159 Rue Maréchal Cruchot";
        String address2      = "36 Quai des Orfèvres";
        List<Firestation> firestations = List.of(new Firestation(address1, stationNumber),
                                                 new Firestation(address2, stationNumber));
        when(jSonFirestationRepository.findByStationNumber(stationNumber)).thenReturn(firestations);
        when(jSonPersonRepository.findByAddress(any(String.class))).thenReturn(people);
        when(jSonMedicalRecordRepository.getMedicationsByName(any(String.class), any(String.class))).thenReturn(Collections.emptyList());
        when(jSonMedicalRecordRepository.getAllergiesByName(any(String.class), any(String.class))).thenReturn(Collections.emptyList());

        FloodViewModel result = jSonFirestationService.getCoveredHouseholds(stationNumber);

        assertTrue(result.getHouseholds().containsKey(address1));
        assertTrue(result.getHouseholds().containsKey(address2));
        FirePersonViewModel firstPersonAtAddress1 = result.getHouseholds().get(address1).stream().findFirst().get();

        assertThat(firstPersonAtAddress1.getLastName()).isEqualTo(person1.getLastName());
        assertThat(firstPersonAtAddress1.getPhone()).isEqualTo(person1.getPhone());
        assertThat(firstPersonAtAddress1.getAge()).isEqualTo(38);
        assertTrue(firstPersonAtAddress1.getAllergies().isEmpty());
        assertTrue(firstPersonAtAddress1.getMedications().isEmpty());
    }
}