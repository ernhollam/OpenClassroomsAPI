package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Firestation;

import java.util.List;
import java.util.Optional;

public interface FirestationRepository {

    /**
     * Save firestation.
     *
     * @param firestation
     *         Firestation to save
     *
     * @return firestation saved
     */
    Firestation save(Firestation firestation) throws Exception;

    /**
     * Delete firestation with specified station number.
     *
     * @param stationNumber
     *         Station number of fire station to delete
     */
    void deleteByStationNumber(int stationNumber) throws Exception;

    /**
     * Delete firestation with specified address.
     *
     * @param address
     *         ID of firestation to delete
     */
    void deleteByAddress(String address) throws Exception;

    /**
     * Get list of all firestations.
     *
     * @return list of firestations.
     */
    List<Firestation> findAll();

    /**
     * Find fire stations with specified station number.
     *
     * @param stationNumber
     *         station number of fire station to find
     *
     * @return Found fire station
     */
    List<Firestation> findByStationNumber(int stationNumber);

    /**
     * Find firestation with specified address.
     *
     * @param address
     *         Address of fire station to find
     *
     * @return Found fire station
     */
    Optional<Firestation> findByAddress(String address);
}
