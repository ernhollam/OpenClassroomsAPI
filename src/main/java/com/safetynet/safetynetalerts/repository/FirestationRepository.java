package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Firestation;

import java.util.Optional;

public interface FirestationRepository {

    /**
     * Save firestation.
     *
     * @param firestation Firestation to save
     *
     * @return firestation saved
     */
    Firestation save(Firestation firestation);

    /**
     * Delete firestation with specified id.
     *
     * @param id ID of firestation to delete
     */
    void deleteById(Long id);

    /**
     * Get list of all firestations.
     *
     * @return list of firestations.
     */
    Iterable<Firestation> findAll();

    /**
     * Find firestation with specified ID.
     *
     * @param id ID of firestation to find
     *
     * @return Found firestation
     */
    Optional<Firestation> findById(Long id);
}
