package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.service.JSonPersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChildAlertController.class)
class ChildAlertControllerTest {
    @Autowired
    private MockMvc           mockMvc;
    @MockBean
    private JSonPersonService jSonPersonService;

    @Test
    void getChildAlert() throws Exception {
        mockMvc.perform(get("/childAlert")
                                .param("address", "1509 Culver St"))
               .andDo(print())
               .andExpect(status().isOk());
    }
}