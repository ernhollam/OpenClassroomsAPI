package com.safetynet.safetynetalerts.repository;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.safetynet.safetynetalerts")
@Data
public class DataPathProperties {
    /**
     * Path to data source.
     */
        private String datasource;
}
