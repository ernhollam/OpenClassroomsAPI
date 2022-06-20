package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.safetynet.safetynetalerts.configuration.DataPathConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;


@Slf4j
@Repository
public class JSonRepository {
    /**
     * Path to JSON file.
     */
    private final String                      datasource;
    /**
     * JSON file.
     */
    private final File                        jsonFile;
    /**
     * Object mapper.
     */
    private final ObjectMapper                mapper;

    /**
     * JSonRepository constructor
     *
     * @param dataPathConfiguration
     *         Path to application properties
     * @param mapperBuilder
     *         Mapper builder
     */

    public JSonRepository(DataPathConfiguration dataPathConfiguration, Jackson2ObjectMapperBuilder mapperBuilder) {
        this.datasource = dataPathConfiguration.getDatasource();
        this.jsonFile = new File(datasource);
        mapper = mapperBuilder.build();
    }


    /**
     * Reads all data from Json file.
     *
     * @return a json node
     */
    public JsonNode readData() {
        // prefer returning a NullNode object instead of a null value
        JsonNode rootNode = NullNode.getInstance();
        log.debug("Data source path: {}", datasource);
        log.debug("Reading JSON file {}", jsonFile);

        try {
            rootNode = mapper.readTree(jsonFile);
            log.debug("Resulting JsonNode read from file: {}",
                      rootNode);
            return rootNode;
        } catch (IOException ioException) {
            log.error("Error while reading JSON file: ", ioException);
        }
        return rootNode;
    }

    /**
     * Writes new node to Json file.
     *
     * @param rootNode
     *         New data to add to JSON file true if no error occurred
     */
    public void writeData(JsonNode rootNode) {
        log.debug("Writing data {} into JSON file {}", rootNode, jsonFile);
        try {
            mapper.writeValue(jsonFile, rootNode);
            log.debug("Data {} was successfully written.", rootNode);
        } catch (IOException ioException) {
            log.error("Failed to write data {} into file {}: {}",
                      rootNode.toString(), jsonFile, ioException);
        }
    }

    /**
     * Gets specified node.
     *
     * @param nodeName
     *         String to specify the name of the node
     *
     * @return required node as ArrayNode
     */
    public JsonNode getNode(String nodeName) {
        JsonNode rootNode = readData(); // Get root node
        if (nodeName.equals("root")) {
            return rootNode;
        } else {
            return rootNode.path(nodeName);
        }
    }


}
