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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
@Data
@Slf4j
public class JSonFirestationRepository implements IFirestationRepository {

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
     * @param firestationToSave Firestation to save
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
     * Updates firestation with given station number.
     *
     * @param firestation ID of station du update
     */
    public Firestation update(Firestation firestation) throws Exception {
        int                   stationNumber           = firestation.getStation();
        Optional<Firestation> firestationInDataSource = findByStationNumber(stationNumber);

        if (firestationInDataSource.isEmpty()) {
            String notFoundMessage = "Firestation n°" + stationNumber + " does not exist.";
            log.error(notFoundMessage);
            throw new ResourceNotFoundException(notFoundMessage);
        } else {
            Firestation firestationToBeUpdated = firestationInDataSource.get();
            //TODO demander s'il faut mettre à jour tout le temps ou seulement si le champ est différent
            String address = firestation.getAddress();
            if (address != null) {
                firestationToBeUpdated.setAddress(address);
            }
            return save(firestationToBeUpdated);
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
     * Find firestation with specified ID in JSon file.
     *
     * @param stationNumber ID of firestation to find
     *
     * @return Found firestation
     */
    @Override
    public Optional<Firestation> findByStationNumber(int stationNumber) {
        Optional<Firestation> foundStation = Optional.empty();
        Iterable<Firestation> firestations = getFirestationsFromJsonFile();

        for (Firestation firestation : firestations) {
            if (firestation.getStation() == stationNumber) {
                foundStation = Optional.of(firestation);
                log.debug("Found firestation: {}", foundStation);
                break;
            }

        }
        return foundStation;
    }


    /**
     * Delete firestation with specified station number from JSon file.
     *
     * @param stationNumber Number of firestation to delete
     */
    @Override
    public void deleteByStationNumber(int stationNumber) throws Exception {
        // find if firestation exists
        Optional<Firestation> firestationToDelete = findByStationNumber(stationNumber);
        // create iterator to browse list of firestations and delete it if it exists
        Iterable<Firestation> firestations = getFirestationsFromJsonFile();
        if (firestationToDelete.isPresent()) {
            Iterator<Firestation> iterator = firestations.iterator();
            while (iterator.hasNext()) {
                Firestation firestation = iterator.next();
                if (firestation.getStation() == firestationToDelete.get().getStation()) {
                    iterator.remove();
                }
            }
            // update firestations node
            JsonNode updatedFirestationsNode = firestationMapper.valueToTree(firestations);
            JsonNode rootNode                = jSonRepository.getNode("root");
            updateFirestationsNode((ObjectNode) rootNode, updatedFirestationsNode);
            boolean success = jSonRepository.writeJsonFile(rootNode);
            if (success) {
                log.debug("Deleted firestation n°{}", stationNumber);
            } else {
                log.error("Error when updating JSON file after deletion of station n°{}", stationNumber);
                throw new Exception("Failed to update JSON file after deletion of firestation n°" + stationNumber);
            }
        } else {
            log.error("The firestation n°{} that you are trying to delete does not exist in JSON file.", stationNumber);
            throw new Exception("The firestation n°"
                                + stationNumber
                                + " that you are trying to delete does not exist"
                                + " " +
                                "in JSON file.");
        }
    }

    /**
     * Overwrites root node with updated list of firestations.
     *
     * @param rootNode                Root node
     * @param updatedFirestationsNode Firestations node updated
     */
    private void updateFirestationsNode(ObjectNode rootNode, JsonNode updatedFirestationsNode) {
        rootNode.replace("firestations", updatedFirestationsNode);
    }
}
