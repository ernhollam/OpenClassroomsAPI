package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Data
@Slf4j
public class JSonFirestationRepository implements FirestationRepository {

    private final JSonRepository jSonRepository;
    private final ObjectMapper   firestationMapper = new ObjectMapper();

    public JSonFirestationRepository(JSonRepository jSonRepository) {
        this.jSonRepository = jSonRepository;
    }

    /**
     * Reads Json file and returns a list of all firestations in file.
     *
     * @return a list of Firestations.
     */
    private List<Firestation> getFirestationsFromJsonFile() {
        final JsonNode firestationsNode = jSonRepository.getNode("firestations");

        if (firestationsNode.isEmpty()) {
            log.error("No firestations exist in JSON file.");
            return Collections.emptyList();
        } else {
            List<Firestation> firestations = firestationMapper.
                    convertValue(firestationsNode,
                                 new TypeReference<>() {
                                 }); // Use TypeReference List<Firestation> to avoid casting all the
            // time when this method is called
            log.debug("Found list of firestations: {}", firestations);
            return firestations;
        }
    }

    /**
     * Save firestation into JSon file.
     *
     * @param firestationToSave
     *         Firestation to save
     *
     * @return firestation saved
     */
    @Override
    public Firestation save(Firestation firestationToSave) throws Exception {
        // Get useful nodes
        JsonNode rootNode         = jSonRepository.getNode("root");
        JsonNode firestationsNode = jSonRepository.getNode("firestations");
        // Transform Firestation object into Json node and add to persons node
        JsonNode firestationToSaveAsNode = firestationMapper.valueToTree(firestationToSave);
        ((ArrayNode) firestationsNode).add(firestationToSaveAsNode);
        // Overwrite root node with new persons node
        updateFirestationsNode((ObjectNode) rootNode, firestationsNode);
        //Write data
        boolean success          = jSonRepository.writeJsonFile(rootNode);
        int     newStationNumber = firestationToSave.getStation();
        if (success) {
            log.debug("Saved new firestation n°{}.", newStationNumber);
            return firestationToSave;
        } else {
            log.error("Failed to save new firestation n°{}.", newStationNumber);
            throw new Exception("Failed to save firestation.");
        }
    }


    /**
     * Get list of all firestations in JSON file.
     *
     * @return list of firestations.
     */
    @Override
    public List<Firestation> findAll() {
        return getFirestationsFromJsonFile();
    }

    /**
     * Find firestation with specified address in JSon file.
     *
     * @param address
     *         Fire station's address
     *
     * @return Found fire station
     */
    @Override
    public Optional<Firestation> findByAddress(String address) {
        Optional<Firestation> foundStation = Optional.empty();
        Iterable<Firestation> firestations = getFirestationsFromJsonFile();

        for (Firestation firestation : firestations) {
            if (firestation.getAddress().equalsIgnoreCase(address)) {
                foundStation = Optional.of(firestation);
                log.debug("Found firestation: {}", foundStation);
                break;
            }
        }
        return foundStation;
    }

    /**
     * Finds stations by station number.
     *
     * @param stationNumber
     *         Station number of fire stations' addresses to find
     *
     * @return a list of fire stations
     */
    public List<Firestation> findByStationNumber(int stationNumber) {
        ArrayList<Firestation> foundStations = new ArrayList<>();
        List<Firestation>      firestations  = getFirestationsFromJsonFile();

        for (Firestation firestation : firestations) {
            if (firestation.getStation() == stationNumber) {
                foundStations.add(firestation);
            }
        }
        return foundStations;
    }


    /**
     * Delete firestation with specified station number from JSon file.
     *
     * @param stationNumber
     *         Number of firestation to delete
     */
    @Override
    public void deleteByStationNumber(int stationNumber) throws Exception {
        // find if firestation exists
        List<Firestation> firestationsToDelete = findByStationNumber(stationNumber);
        // create iterator to browse list of firestations and delete it if it exists
        List<Firestation> firestationsInDataSource = getFirestationsFromJsonFile();
        if (firestationsToDelete.isEmpty()) {
            throw new ResourceNotFoundException("There is no fire station with station number " + stationNumber + ".");
        } else {
            // browse list and delete if found
            firestationsInDataSource.removeIf(firestation -> firestation.getStation() == stationNumber);
            // update firestations node
            JsonNode updatedFirestationsNode = firestationMapper.valueToTree(firestationsInDataSource);
            JsonNode rootNode                = jSonRepository.getNode("root");
            updateFirestationsNode((ObjectNode) rootNode, updatedFirestationsNode);
            boolean success = jSonRepository.writeJsonFile(rootNode);
            if (success) {
                log.debug("Deleted firestation n°{}", stationNumber);
            } else {
                log.error("Error when updating JSON file after deletion of station n°{}", stationNumber);
                throw new Exception("Failed to update JSON file after deletion of firestation n°" + stationNumber);
            }
        }
    }

    /**
     * Delete firestation with specified address.
     *
     * @param address
     *         Number of firestation to delete
     */
    @Override
    public void deleteByAddress(String address) throws Exception {
        Optional<Firestation> firestationToDelete      = findByAddress(address);
        List<Firestation>     firestationsInDataSource = getFirestationsFromJsonFile();

        if (firestationToDelete.isEmpty()) {
            throw new ResourceNotFoundException("There is no fire station at the following address: " + address +
                                                ".");
        } else {
            // browse list and delete if found
            firestationsInDataSource.removeIf(firestation -> firestation.getAddress().equalsIgnoreCase(firestationToDelete.get().getAddress()));
        }
        // update firestations node
        JsonNode updatedFirestationsNode = firestationMapper.valueToTree(firestationsInDataSource);
        JsonNode rootNode                = jSonRepository.getNode("root");
        updateFirestationsNode((ObjectNode) rootNode, updatedFirestationsNode);
        boolean success = jSonRepository.writeJsonFile(rootNode);
        if (!success) {
            throw new Exception("Failed to update JSON file after deletion of firestation with the following " +
                                "address:\n" + address);
        }
    }

    /**
     * Overwrites root node with updated list of firestations.
     *
     * @param rootNode
     *         Root node
     * @param updatedFirestationsNode
     *         Firestations node updated
     */
    private void updateFirestationsNode(ObjectNode rootNode, JsonNode updatedFirestationsNode) {
        rootNode.replace("firestations", updatedFirestationsNode);
    }
}
