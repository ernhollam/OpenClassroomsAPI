package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

@Data
@Slf4j
public abstract class JSonRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DataPathProperties dataPathProperties;

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
}
