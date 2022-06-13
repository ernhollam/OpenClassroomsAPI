package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationViewModel;

import java.util.List;

/**
 * Get, delete or save a firestation from/to a datasource.
 */
public interface FirestationService {
    /**
     * Gets firestation.
     *
     * @param stationNumber
     *         Address of firestation to get
     *
     * @return Firestation a firestation if not empty
     */
    List<Firestation> getFirestation(final int stationNumber);

    /**
     * Gets the list of all firestations.
     *
     * @return an iterable of Firestations
     */
    Iterable<Firestation> getFirestations();

    /**
     * Deletes firestation with given id.
     *
     * @param id
     *         ID of firestation to delete
     */
    void deleteFirestation(final int id) throws Exception;

    /**
     * Saves firestation.
     *
     * @param firestation
     *         Firestation to save
     *
     * @return Firestation
     */
    Firestation saveFirestation(final Firestation firestation) throws Exception;

    /**
     * Updates firestation with given station number.
     *
     * @param firestation
     *         Firestation to update
     */
    Firestation updateFirestation(final Firestation firestation) throws Exception;

    FirestationViewModel getPeopleCoveredByStation(int stationNumber);
}
