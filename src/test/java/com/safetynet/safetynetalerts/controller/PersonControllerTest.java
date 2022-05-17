package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JSonRepository;
import com.safetynet.safetynetalerts.service.PersonService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(PersonController.class) // instantiate PersonController only for this test
public class PersonControllerTest {
    @Autowired
    private MockMvc       mockMvc;
    @MockBean
    private PersonService personService;

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
        when(personService.getPersons()).thenReturn(listPersons);
        mockMvc.perform(get("/person"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].firstName", is("John")))
               .andExpect(jsonPath("$[1].firstName", is("Felicia")));
    }

    @Test
    public void getPersons_shouldReturn_NoContentCode() throws Exception {
        when(personService.getPersons()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/person"))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$.firstName").doesNotExist());
    }

    @Test
    public void getPerson_shouldReturn_OnePerson_WhenExistsInFile() throws Exception {
        String firstName = feliciaBoyd.getFirstName();
        String lastName  = feliciaBoyd.getLastName();
        when(personService.getPersonByName(any(String.class), any(String.class))).thenReturn(Optional.of(feliciaBoyd));


        mockMvc.perform(get("/person")
                                .param("firstName", firstName)
                                .param("lastName", lastName))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].firstName", is(firstName)))
               .andExpect(jsonPath("$[0].lastName", is(lastName)))
               .andExpect(jsonPath("$[0].address", is(feliciaBoyd.getAddress())))
               .andExpect(jsonPath("$[0].city", is(feliciaBoyd.getCity())))
               .andExpect(jsonPath("$[0].zip", is(feliciaBoyd.getZip())))
               .andExpect(jsonPath("$[0].phone", is(feliciaBoyd.getPhone())))
               .andExpect(jsonPath("$[0].email", is(feliciaBoyd.getEmail())));
    }

    @Test
    public void getPerson_shouldThrow_ResourceNotFoundException_whenPersonDoesNotExist() throws Exception {
        String firstName = "Shawna";
        String lastName  = "Stelzer";
        when(personService.getPersonByName(any(String.class), any(String.class))).thenReturn(Optional.empty());


        mockMvc.perform(get("/person")
                                .param("firstName", firstName)
                                .param("lastName", lastName))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    public void getPerson_shouldReturn_BadRequest_WithFirstNameOnly() throws Exception {
        when(personService.getPersonByName(any(String.class), any(String.class))).thenReturn(Optional.empty());


        mockMvc.perform(get("/person")
                                .param("firstName", feliciaBoyd.getFirstName()))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    public void getPerson_shouldReturn_BadRequest_WithLastNameOnly() throws Exception {
        when(personService.getPersonByName(any(String.class), any(String.class))).thenReturn(Optional.empty());


        mockMvc.perform(get("/person")
                                .param("lastName", johnBoyd.getLastName()))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    public void savePerson_whenPersonDoesNotAlreadyExist() throws Exception {
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
        when(personService.savePerson(person)).thenReturn(person);


        mockMvc.perform(post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSonRepository.toJsonString(person)))
               .andDo(print())
               .andExpect(status().isCreated());
    }


    @Test
    public void deletePerson_shouldDelete_whenPersonExists() {
        //TODO IT test for delete when person exists
    }

    @Test
    public void deletePerson_shouldNotDelete_whenPersonDoesNotExist() {
        //TODO IT test for delete when person does not exist
    }

    @Test
    public void updatePerson_shouldUpdate_whenPersonExists() {
        // TODO IT test for update when person exists
    }

    @Test
    public void updatePerson_shouldNotUpdate_whenPersonDoesNotExist() {
        // TODO IT test for update when person does not exist
    }
}
