package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Firestation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JSonFirestationRepository implements FirestationRepository {

    /**
     * Save firestation into JSon file.
     *
     * @param firestation Firestation to save
     *
     * @return firestation saved
     */
    @Override
    public Firestation save(Firestation firestation) {
        return null;
    }

    /**
     * Delete firestation with specified id from JSon file.
     *
     * @param id ID of firestation to delete
     */
    @Override
    public void deleteById(Long id) {

    }

    /**
     * Get list of all firestations in JSON file.
     *
     * @return list of firestations.
     */
    @Override
    public Iterable<Firestation> findAll() {
        return null;
    }

    /**
     * Find firestation with specified ID in JSon file.
     *
     * @param id ID of firestation to find
     *
     * @return Found firestation
     */
    @Override
    public Optional<Firestation> findById(Long id) {
        return Optional.empty();
    }
}
