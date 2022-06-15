package com.safetynet.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.JSonFirestationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
@WebMvcTest(FirestationController.class) // instantiate FirestationController only for this test
public class FirestationControllerTest {
    @Autowired
    private MockMvc                mockMvc;
    @MockBean
    private JSonFirestationService jSonFirestationService;

    private List<Firestation> listFirestations;
    private Firestation       firestation1;
    private ObjectMapper      mapper;

    @BeforeAll
    void setup() {

        final Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();
        mapper = mapperBuilder.build();

        firestation1 = new Firestation("18 rue des fontaines",
                                       1);
        Firestation firestation2 = new Firestation("159 boulevard charles de gaulle",
                                                   2);
        listFirestations = List.of(firestation1, firestation2);
    }

    @Test
    public void getFirestations_shouldReturn_ListOfAllFirestations() throws Exception {
        when(jSonFirestationService.getFirestations()).thenReturn(listFirestations);
        mockMvc.perform(get("/firestations"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].address", is("18 rue des fontaines")))
               .andExpect(jsonPath("$[1].address", is("159 boulevard charles de gaulle")));
    }


    @Test
    public void getFirestation_shouldReturn_OneFirestation_WhenExistsInFile() throws Exception {
        int station = 1;
        when(jSonFirestationService.getFirestation(any(int.class))).thenReturn(List.of(firestation1));


        mockMvc.perform(get("/firestation/{stationNumber}", station))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    public void getFirestation_shouldThrow_ResourceNotFoundException_whenFirestationDoesNotExist() throws Exception {
        int station = 3;
        when(jSonFirestationService.getFirestation(any(int.class))).thenThrow(ResourceNotFoundException.class);


        mockMvc.perform(get("/firestation/{stationNumber}", station))
               .andDo(print())
               .andExpect(status().isNotFound());
    }


    @Test
    public void saveFirestation_shouldReturn_created_whenFirestationDoesNotAlreadyExist() throws Exception {
        Firestation firestation3 = new Firestation("1 Rue Colomb", 3);
        when(jSonFirestationService.saveFirestation(any(Firestation.class))).thenReturn(firestation3);


        mockMvc.perform(post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(firestation3)))
               .andDo(print())
               .andExpect(status().isCreated());
    }


    @Test
    public void deleteFirestation_shouldReturn_noContent_whenFirestationExists() throws Exception {
        int station = 1;

        mockMvc.perform(delete("/firestation/{stationNumber}", station))
               .andDo(print())
               .andExpect(status().isNoContent());
    }

    @Test
    public void deleteFirestation_shouldReturn_notFound_whenFirestationDoesNotExist() throws Exception {
        int station = 150;

        doThrow(ResourceNotFoundException.class).when(jSonFirestationService).deleteFirestation(any(int.class));

        mockMvc.perform(delete("/firestation/{stationNumber}", station))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    public void updateFirestation_shouldReturn_ok_whenFirestationExists() throws Exception {
        firestation1.setStation(3);
        when(jSonFirestationService.updateFirestation(firestation1)).thenReturn(firestation1);

        mockMvc.perform(put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(firestation1)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    public void updateFirestation_shouldNotUpdate_whenFirestationDoesNotExist() throws Exception {
        Firestation firestation3 = new Firestation("1 Rue Colomb", 3);

        when(jSonFirestationService.updateFirestation(any(Firestation.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(firestation3)))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    void getPeopleCoveredByStation() throws Exception {
        mockMvc.perform(get("/firestation")
                                .param("stationNumber", "3"))
               .andDo(print())
               .andExpect(status().isOk());
    }
}
