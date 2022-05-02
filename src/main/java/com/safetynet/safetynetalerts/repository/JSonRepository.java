package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Data
@Slf4j
@Repository
public class JSonRepository {
    /**
     * Property object with path to data source.
     */
    private final DataPathProperties dataPathProperties;
    /**
     * Path to JSON file.
     */
    private final String             datasource;
    /**
     * JSON file.
     */
    private final File jsonFile;
    /**
     * Object mapper.
     */
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty print globally

    public JSonRepository(DataPathProperties dataPathProperties) {
        this.dataPathProperties = dataPathProperties;
        this.datasource = dataPathProperties.getDatasource();
        this.jsonFile = new File(datasource);
    }


    /**
     * Reads all data from Json file.
     *
     * @return a json node
     */
    public JsonNode readJsonFile() {
        // prefer returning a NullNode object instead of a null value
        JsonNode rootNode = NullNode.getInstance();
        log.debug("Data source path: {}", datasource);
        log.debug("Reading JSON file {}", jsonFile);

        try {
            rootNode = mapper.readTree(jsonFile);
            log.info("Resulting JsonNode read from file: {}",
                     rootNode);
            return rootNode;
        } catch (IOException ioException) {
            log.error("Error while reading JSON file: ", ioException);
        }
        return rootNode;
    }

    /**
     * Gets specified node.
     *
     * @param nodeName String to specify the name of the node
     *
     * @return required node as ArrayNode
     */
    public JsonNode getNode(String nodeName) {
        JsonNode rootNode = readJsonFile(); // Get root node
        if (nodeName.equals("root")) {
            return rootNode;
        } else {
            return rootNode.path(nodeName);
        }
    }

    /**
     * Writes new node to Json file.
     *
     * @param rootNode New data to add to JSON file
     *
     * @return true if no error occurred
     */
    public boolean writeJsonFile(JsonNode rootNode) {
        log.debug("Writing data {} into JSON file {}", rootNode, jsonFile);

        try {
            mapper.writeValue(jsonFile, rootNode);
            return true;
        } catch (IOException ioException) {
            log.error("Failed to write data {} into file {}: {}",
                      rootNode.toString(), jsonFile, ioException);
            return false;
        }
    }
}
