package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FloodViewModel;

import java.util.List;
import java.util.Set;

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
     * @param address
     *         ID of firestation to delete
     */
    void deleteFirestation(final String address) throws Exception;

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

    /**
     * Returns a list of all phone numbers of people covered by a fire station.
     *
     * @param stationNumber
     *         Station number for which the list of covered people's phone number is wanted.
     *
     * @return List of phone numbers.
     */
    Set<String> getPhoneAlert(int stationNumber);

    /**
     * Returns list of households covered by a station. The list contains the name of each person living in the
     * household, including medical record.
     *
     * @param stations
     *         Station number for which list of covered households is wanted.
     *
     * @return List of households and their inhabitants.
     */
    FloodViewModel getCoveredHouseholds(List<Integer> stations);
}
