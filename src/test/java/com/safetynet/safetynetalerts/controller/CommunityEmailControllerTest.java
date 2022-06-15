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

@WebMvcTest(CommunityEmailController.class)
class CommunityEmailControllerTest {
    @Autowired
    private MockMvc           mockMvc;
    @MockBean
    private JSonPersonService jSonPersonService;

    @Test
    void getCommunityEmail() throws Exception {

        mockMvc.perform(get("/communityEmail")
                                .param("city", "Culver"))
               .andDo(print())
               .andExpect(status().isOk());
    }
}