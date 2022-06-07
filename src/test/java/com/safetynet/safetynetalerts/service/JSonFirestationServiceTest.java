package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.JSonFirestationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
}