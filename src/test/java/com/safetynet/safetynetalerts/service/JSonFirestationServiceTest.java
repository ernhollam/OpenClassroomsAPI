package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationPersonViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationViewModel;
import com.safetynet.safetynetalerts.repository.JSonFirestationRepository;
import com.safetynet.safetynetalerts.repository.JSonMedicalRecordRepository;
import com.safetynet.safetynetalerts.repository.JSonPersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
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
        when(jSonPersonRepository.findByAddress(any(String.class))).thenReturn(listPeople);

        when(jSonMedicalRecordRepository.getBirthDateByName(person1.getFirstName(), person1.getLastName()))
                .thenReturn(LocalDate.of(1984, 3, 6));
        when(jSonMedicalRecordRepository.getBirthDateByName(person2.getFirstName(), person2.getLastName()))
                .thenReturn(LocalDate.of(1986, 1, 8));
        when(jSonMedicalRecordRepository.getBirthDateByName(person3.getFirstName(), person3.getLastName()))
                .thenReturn(LocalDate.of(2012, 2, 18));


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

    //TODO getPhoneAlert() tests
    @Test
    void getPhoneAlert_shouldReturn_aListOfPhoneNumbers() {
    }

    @Test
    void getPhoneAlert_shouldReturn_emptyList() {

    }
}