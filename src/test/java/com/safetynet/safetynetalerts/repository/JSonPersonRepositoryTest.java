package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.model.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JSonPersonRepositoryTest {

    private static final String       jsonDataPath = "src/test/resources/data_test.json";
    private static final File         jsonFile     = new File(jsonDataPath);
    private static final ObjectMapper mapper       = new ObjectMapper();
    private static       Iterable<Person> people;

    PersonRepository jsonPersonRepository = new JSonPersonRepository();

    @BeforeAll
    public static void setUp() {
        try {
            JsonNode completeDataFromJsonAsNode = mapper.readTree(jsonFile);
            JsonNode personsNode                = completeDataFromJsonAsNode.get("persons");
            people = mapper.convertValue(personsNode, new TypeReference<>() {
            });
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    public void findAll() {
        List<Person> testedPeople = (List<Person>) jsonPersonRepository.findAll();

        assertThat(testedPeople.size()).isEqualTo(6);
    }

    @Test
    void save() {
    }

    @Test
    void deleteByName() {
    }

    @Test
    void findByName() {
    }
}