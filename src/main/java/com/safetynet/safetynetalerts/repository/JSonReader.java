package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.safetynet.safetynetalerts.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Reads all data from Json file.
 */
@Slf4j
public class JSonReader {
    private static final ObjectMapper       mapper = new ObjectMapper();
    @Autowired
    private static       DataPathProperties dataPathProperties;

    /**
     * Reads all data from Json file.
     *
     * @return a json node
     */
    public JsonNode readJsonFile() {
        JsonNode     completeDataFromJsonAsNode = NullNode.getInstance(); // prefer returning a NullNode object instead of a null value
        final String jsonDataPath               = dataPathProperties.getDatasource();
        final File   jsonFile                   = new File(jsonDataPath);

        log.debug("Reading JSON file {}", jsonFile);
        try {
            completeDataFromJsonAsNode = mapper.readTree(jsonFile);
            log.info("Resulting JsonNode read from file: {}", completeDataFromJsonAsNode);
            return completeDataFromJsonAsNode;
        } catch (IOException ioException) {
            log.error("Error while reading JSON file: ", ioException);
        }
        return completeDataFromJsonAsNode;
    }

    /**
     * Reads Json file and returns a list of Person.
     *
     * @return a list of Person.
     */
    public Iterable<Person> getPeopleFromJsonFile() {
        JsonNode completeDataFromJsonAsNode = readJsonFile();
        if (completeDataFromJsonAsNode.isEmpty()) {
            return Collections.emptySet();
        } else {
            final JsonNode personsNode = completeDataFromJsonAsNode.get("persons");
            return mapper.convertValue(personsNode, new TypeReference<>() {
            });
        }
    }
}
