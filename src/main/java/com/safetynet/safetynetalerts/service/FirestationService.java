package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.IFirestationRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class FirestationService implements IFirestationService {

    /**
     * Instance of IFirestationRepository.
     */
    @Autowired
    private IFirestationRepository IFirestationRepository;

    /**
     * Get firestation.
     *
     * @param id ID of firestation to get
     *
     * @return Firestation a firestation if not empty
     */
    @Override
    public Optional<Firestation> getFirestation(final int id) {
        return IFirestationRepository.findByStationNumber(id);
    }

    /**
     * Get the list of all firestations.
     *
     * @return an iterable of Firestations
     */
    @Override
    public Iterable<Firestation> getFirestations() {
        return IFirestationRepository.findAll();
    }

    /**
     * Delete firestation with given id.
     *
     * @param id ID of firestation to delete
     */
    @Override
    public void deleteFirestation(final int id) throws Exception {
        IFirestationRepository.deleteByStationNumber(id);
    }

    /**
     * Save firestation.
     *
     * @param firestation Firestation to save
     *
     * @return Firestation
     */
    @Override
    public Firestation saveFirestation(final Firestation firestation) throws Exception {
        return IFirestationRepository.save(firestation);
    }

    /**
     * Updates firestation with given station number.
     *
     * @param firestation Firestation to update
     */
    @Override
    public Firestation updateFirestation(final Firestation firestation) throws Exception {
        return IFirestationRepository.update(firestation);
    }
}
