package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Data
@Slf4j
public abstract class JSonRepository {
    /**
     * Object mapper.
     */
    static  ObjectMapper       mapper = new ObjectMapper();
    /**
     * Property object with path to data source.
     */
    @Autowired
    private DataPathProperties dataPathProperties;
    /**
     * Path to JSON file.
     */
    private String             DATASOURCE = dataPathProperties.getDatasource() ;
    /**
     * JSON file.
     */
    private    File          JSON_FILE  = new File(DATASOURCE);

    /**
     * Reads all data from Json file.
     *
     * @return a json node
     */
    public JsonNode readJsonFile() {
        // prefer returning a NullNode object instead of a null value
        JsonNode completeDataFromJsonAsNode = NullNode.getInstance();
        log.debug("Data source path: {}", DATASOURCE);
        log.debug("Reading JSON file {}", JSON_FILE);

        try {
            completeDataFromJsonAsNode = mapper.readTree(JSON_FILE);
            log.info("Resulting JsonNode read from file: {}",
                     completeDataFromJsonAsNode);
            return completeDataFromJsonAsNode;
        } catch (IOException ioException) {
            log.error("Error while reading JSON file: ", ioException);
        }
        return completeDataFromJsonAsNode;
    }

    /**
     * Writes new node to Json file.
     *
     * @param nodeToAdd New data to add to JSON file
     *
     * @return true if no error occurred
     */
    public boolean writeJsonFile(JsonNode nodeToAdd) {
        log.debug("Writing data {} into JSON file {}", nodeToAdd, JSON_FILE);
        nodeToAdd = mapper.valueToTree(nodeToAdd);
        ArrayNode completeData = (ArrayNode) readJsonFile();
        completeData.add(nodeToAdd);

        try (FileWriter fw = new FileWriter(JSON_FILE)){
            fw.write(completeData.toPrettyString());
            return true;
        } catch (IOException ioException) {
            log.error("Failed to write data {} into file {}: {}",
                      nodeToAdd.toString(), JSON_FILE, ioException);
            return false;
        }
    }
}
