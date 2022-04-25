package com.safetynet.safetynetalerts.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Updates JSON data file.
 */
@Slf4j
public class JSonWriter {
    @Autowired
    private static DataPathProperties dataPathProperties;

    private static final ObjectMapper mapper = new ObjectMapper();
}
