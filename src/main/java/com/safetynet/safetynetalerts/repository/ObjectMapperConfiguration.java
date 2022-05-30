package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ObjectMapperConfiguration {
    @Bean
    // Create bean with configured object mapper to deserialize and serialize implicitly and explicitly LocalDates
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder() {

            @Override
            public void configure(ObjectMapper objectMapper) {
                super.configure(objectMapper);
                // configure mapper to deserialize dates to LocalDate
                JavaTimeModule        module                = new JavaTimeModule();
                DateTimeFormatter     dateTimeFormatter     = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer(dateTimeFormatter);
                LocalDateSerializer   localDateSerializer   = new LocalDateSerializer(dateTimeFormatter);
                module.addDeserializer(LocalDate.class, localDateDeserializer)
                      .addSerializer(localDateSerializer);

                objectMapper.registerModule(module)
                            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                            .enable(SerializationFeature.INDENT_OUTPUT);
            }

        };
    }

}
