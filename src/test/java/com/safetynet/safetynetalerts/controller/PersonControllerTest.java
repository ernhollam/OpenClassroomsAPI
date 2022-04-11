package com.safetynet.safetynetalerts.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.PersonService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class) // instantiate PersonController only for this test
public class PersonControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static       List<Person> listPersons;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @BeforeAll
    static void setUp() throws IOException {
        // Read list from json file and transform it into a Java List
        listPersons = objectMapper.readValue(new File("src/test/resources/data_test.json"), new TypeReference<>() {
        });
    }

    @AfterAll
    static void flush() throws IOException {
        objectMapper.writeValue(new File("target/date_test.json"), listPersons);
    }

    @Test
    public void testGetPersons() throws Exception {
        mockMvc.perform(get("/persons"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Warren")));
    }

    @Test
    public void testGetPerson() throws Exception {
        mockMvc.perform(get("/person/")).andExpect(status().isOk());
    }

    @Test
    public void testSavePerson() {

    }

    @Test
    public void testDeletePerson() {

    }
}
