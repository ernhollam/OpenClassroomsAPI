package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private final File               jsonFile;
    /**
     * Object mapper.
     */
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty print globally

    /**
     * JSonRepository constructor
     *
     * @param dataPathProperties
     *         Path to application properties
     */

    public JSonRepository(DataPathProperties dataPathProperties) {
        this.dataPathProperties = dataPathProperties;
        this.datasource = dataPathProperties.getDatasource();
        this.jsonFile = new File(datasource);
    }

    /**
     * Converts any object to Json.
     *
     * @param obj
     *         Any object to convert into a Json String
     *
     * @return Object as json string.
     */
    public static String toJsonString(final Object obj) {
        try {
            log.debug("Converting object {} to Json String", obj);
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("An error occurred when converting object {} to JSON.\n" + e.getMessage());
            return "";
        }
    }

    /**
     * Configures ObjectMapper to deserialize dates as LocalDate.
     *
     * @param mapper
     *         An ObjectMapper
     */
    private void configureMapper(ObjectMapper mapper) {
        // configure mapper to deserialize dates to LocalDate
        JavaTimeModule        module                = new JavaTimeModule();
        DateTimeFormatter     dateTimeFormatter     = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer(dateTimeFormatter);
        LocalDateSerializer   localDateSerializer   = new LocalDateSerializer(dateTimeFormatter);
        module.addDeserializer(LocalDate.class, localDateDeserializer)
              .addSerializer(localDateSerializer);

        mapper.registerModule(module)
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Reads all data from Json file.
     *
     * @return a json node
     */
    public JsonNode readJsonFile() {
        configureMapper(mapper);
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
     * @param nodeName
     *         String to specify the name of the node
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
     * @param rootNode
     *         New data to add to JSON file
     *
     * @return true if no error occurred
     */
    public boolean writeJsonFile(JsonNode rootNode) {
        log.debug("Writing data {} into JSON file {}", rootNode, jsonFile);
        configureMapper(mapper);
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
