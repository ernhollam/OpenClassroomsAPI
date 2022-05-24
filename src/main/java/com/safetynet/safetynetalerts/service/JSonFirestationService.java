package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.FirestationRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
public class JSonFirestationService implements FirestationService {

    /**
     * Instance of FirestationRepository.
     */
    @Autowired
    private FirestationRepository firestationRepository;

    /**
     * Get firestation.
     *
     * @param stationNumber
     *         ID of firestation to get
     *
     * @return Firestation a firestation if not empty
     */
    @Override
    public List<Firestation> getFirestation(final int stationNumber) {
        return firestationRepository.findByStationNumber(stationNumber);
    }

    /**
     * Get the list of all firestations.
     *
     * @return an iterable of Firestations
     */
    @Override
    public List<Firestation> getFirestations() {
        return firestationRepository.findAll();
    }

    /**
     * Delete firestation with given id.
     *
     * @param stationNumber
     *         ID of firestation to delete
     */
    @Override
    public void deleteFirestation(final int stationNumber) throws Exception {
        firestationRepository.deleteByStationNumber(stationNumber);
    }

    /**
     * Save firestation.
     *
     * @param firestation
     *         Firestation to save
     *
     * @return Firestation
     */
    @Override
    public Firestation saveFirestation(final Firestation firestation) throws Exception {
        String                address   = firestation.getAddress();
        Optional<Firestation> duplicate = firestationRepository.findByAddress(address);
        if (duplicate.isPresent()) {
            firestationRepository.deleteByAddress(address);
        }
        return firestationRepository.save(firestation);
    }

    /**
     * Updates firestation with given station number.
     *
     * @param firestation
     *         Firestation to update
     */
    @Override
    public Firestation updateFirestation(final Firestation firestation) throws Exception {
        String address = firestation.getAddress();
        Optional<Firestation> firestationInDataSource =
                firestationRepository.findByAddress(address);
        if (firestationInDataSource.isEmpty()) {
            throw new ResourceNotFoundException("There is no fire station at the following address: " + address + ".");
        } else {
            return saveFirestation(firestation);
        }
    }
}
