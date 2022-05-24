package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JSonRepository;
import com.safetynet.safetynetalerts.service.JSonPersonService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(PersonController.class) // instantiate PersonController only for this test
public class PersonControllerTest {
    @Autowired
    private MockMvc           mockMvc;
    @MockBean
    private JSonPersonService jSonPersonService;

    private List<Person> listPersons;
    private Person       johnBoyd;
    private Person       feliciaBoyd;

    @BeforeAll
    void setup() {
        johnBoyd = new Person("John",
                              "Boyd",
                              "1509 Culver St",
                              "Culver",
                              97451,
                              "841-874-6512",
                              "jaboyd@email.com");
        feliciaBoyd = new Person("Felicia",
                                 "Boyd",
                                 "1509 Culver St",
                                 "Culver",
                                 97451,
                                 "841-874-6544",
                                 "jaboyd@email.com");
        listPersons = List.of(johnBoyd, feliciaBoyd);
    }

    @Test
    public void getPersons_shouldReturn_ListOfAllPeople() throws Exception {
        when(jSonPersonService.getPersons()).thenReturn(listPersons);
        mockMvc.perform(get("/person"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].firstName", is("John")))
               .andExpect(jsonPath("$[1].firstName", is("Felicia")));
    }


    @Test
    public void getPerson_shouldReturn_OnePerson_WhenExistsInFile() throws Exception {
        String firstName = feliciaBoyd.getFirstName();
        String lastName  = feliciaBoyd.getLastName();
        when(jSonPersonService.getPersonByName(any(String.class), any(String.class))).thenReturn(Optional.of(feliciaBoyd));


        mockMvc.perform(get("/person/{firstName}/{lastname}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    public void getPerson_shouldThrow_ResourceNotFoundException_whenPersonDoesNotExist() throws Exception {
        String firstName = "Shawna";
        String lastName  = "Stelzer";
        when(jSonPersonService.getPersonByName(any(String.class), any(String.class))).thenReturn(Optional.empty());


        mockMvc.perform(get("/person/{firstName}/{lastname}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isNotFound());
    }


    @Test
    public void savePerson_shouldReturn_Created_whenPersonDoesNotAlreadyExist() throws Exception {
        String firstName = "Kendrick";
        String lastName  = "Stelzer";
        String address   = "947 E. Rose Dr";
        String city      = "Culver";
        int    zip       = 97451;
        String phone     = "841-874-7784";
        String email     = "bstel@email.com";
        Person person = new Person(firstName,
                                   lastName,
                                   address,
                                   city,
                                   zip,
                                   phone,
                                   email);
        when(jSonPersonService.savePerson(person)).thenReturn(person);


        mockMvc.perform(post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSonRepository.toJsonString(person)))
               .andDo(print())
               .andExpect(status().isCreated());
    }


    @Test
    public void deletePerson_shouldReturn_noContent_whenPersonExists() throws Exception {
        String firstName = johnBoyd.getFirstName();
        String lastName  = johnBoyd.getLastName();

        mockMvc.perform(delete("/person/{firstName}/{lastName}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isNoContent());
    }

    @Test
    public void deletePerson_shouldReturn_notFound_whenPersonDoesNotExist() throws Exception {
        String firstName = "Ross";
        String lastName  = "Geller";

        doThrow(ResourceNotFoundException.class).when(jSonPersonService).deletePerson(firstName, lastName);

        mockMvc.perform(delete("/person/{firstName}/{lastName}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    public void updatePerson_shouldReturn_ok_whenPersonExists() throws Exception {
        johnBoyd.setPhone("841-874-6512");
        johnBoyd.setEmail("johnboyd@email.com");
        when(jSonPersonService.updatePerson(johnBoyd)).thenReturn(johnBoyd);

        mockMvc.perform(put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSonRepository.toJsonString(johnBoyd)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    public void updatePerson_shouldNotUpdate_whenPersonDoesNotExist() throws Exception {
        Person ross = new Person("Ross",
                                 "Geller",
                                 "90 Bedford Street",
                                 "New York",
                                 10014,
                                 "841-874-1234",
                                 "wewereonabreak@friends.com");

        when(jSonPersonService.updatePerson(any(Person.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSonRepository.toJsonString(ross)))
               .andDo(print())
               .andExpect(status().isNotFound());
    }
}
