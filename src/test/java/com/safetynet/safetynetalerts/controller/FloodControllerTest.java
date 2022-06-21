package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.service.JSonFirestationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FloodController.class)
class FloodControllerTest {

    @Autowired
    private MockMvc                mockMvc;
    @MockBean
    private JSonFirestationService jSonFirestationService;

    @Test
    void getPeopleCoveredByStation() throws Exception {
        mockMvc.perform(get("/flood")
                                .param("station", "3"))
               .andDo(print())
               .andExpect(status().isOk());
    }

}