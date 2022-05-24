package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.MedicalRecord;
import com.safetynet.safetynetalerts.repository.JSonRepository;
import com.safetynet.safetynetalerts.service.JSonMedicalRecordService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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
@WebMvcTest(MedicalRecordController.class) // instantiate MedicalRecordController only for this test
public class MedicalRecordControllerTest {
    @Autowired
    private MockMvc                  mockMvc;
    @MockBean
    private JSonMedicalRecordService jSonMedicalRecordService;

    private List<MedicalRecord> listMedicalRecords;
    private MedicalRecord       johnBoyd;
    private MedicalRecord       feliciaBoyd;

    @BeforeAll
    void setup() {
        johnBoyd = new MedicalRecord("John",
                                     "Boyd",
                                     LocalDate.of(1984, 3, 6),
                                     new String[]{"aznol:350mg", "hydrapermazol:100mg"},
                                     new String[]{"nillacilan"});
        feliciaBoyd = new MedicalRecord("Felicia",
                                        "Boyd",
                                        LocalDate.of(1986, 8, 1),
                                        new String[]{"tetracyclaz:650mg"},
                                        new String[]{"xilliathal"});
        listMedicalRecords = List.of(johnBoyd, feliciaBoyd);
    }

    @Test
    public void getMedicalRecords_shouldReturn_ListOfAllMedicalRecords() throws Exception {
        when(jSonMedicalRecordService.getMedicalRecords()).thenReturn(listMedicalRecords);
        mockMvc.perform(get("/medicalRecord"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].firstName", is("John")))
               .andExpect(jsonPath("$[1].firstName", is("Felicia")));
    }


    @Test
    public void getMedicalRecord_shouldReturn_OneMedicalRecord_WhenExistsInFile() throws Exception {
        String firstName = feliciaBoyd.getFirstName();
        String lastName  = feliciaBoyd.getLastName();
        when(jSonMedicalRecordService.getMedicalRecordByName(any(String.class), any(String.class))).thenReturn(Optional.of(feliciaBoyd));


        mockMvc.perform(get("/medicalRecord/{firstName}/{lastname}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    public void getMedicalRecord_shouldThrow_ResourceNotFoundException_whenMedicalRecordDoesNotExist() throws Exception {
        String firstName = "Shawna";
        String lastName  = "Stelzer";
        when(jSonMedicalRecordService.getMedicalRecordByName(any(String.class), any(String.class))).thenReturn(Optional.empty());


        mockMvc.perform(get("/medicalRecord/{firstName}/{lastname}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isNotFound());
    }


    @Test
    public void saveMedicalRecord_shouldReturn_created_whenMedicalRecordDoesNotAlreadyExist() throws Exception {
        String    firstName   = "Kendrick";
        String    lastName    = "Stelzer";
        LocalDate birthdate   = LocalDate.of(2014, 3, 6);
        String[]  medications = {"noxidian:100mg", "pharmacol:2500mg"};
        String[]  allergies   = {};
        MedicalRecord medicalRecord = new MedicalRecord(firstName,
                                                        lastName,
                                                        birthdate,
                                                        medications,
                                                        allergies);
        when(jSonMedicalRecordService.saveMedicalRecord(medicalRecord)).thenReturn(null);


        mockMvc.perform(post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSonRepository.toJsonString(medicalRecord)))
               .andDo(print())
               .andExpect(status().isCreated());
    }


    @Test
    public void deleteMedicalRecord_shouldReturn_noContent_whenMedicalRecordExists() throws Exception {
        String firstName = johnBoyd.getFirstName();
        String lastName  = johnBoyd.getLastName();

        mockMvc.perform(delete("/medicalRecord/{firstName}/{lastName}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isNoContent());
    }

    @Test
    public void deleteMedicalRecord_shouldReturn_notFound_whenMedicalRecordDoesNotExist() throws Exception {
        String firstName = "Ross";
        String lastName  = "Geller";

        doThrow(ResourceNotFoundException.class).when(jSonMedicalRecordService).deleteMedicalRecord(firstName,
                                                                                                    lastName);

        mockMvc.perform(delete("/medicalRecord/{firstName}/{lastName}", firstName, lastName))
               .andDo(print())
               .andExpect(status().isNotFound());
    }

    @Test
    public void updateMedicalRecord_shouldReturn_ok_whenMedicalRecordExists() throws Exception {
        johnBoyd.setAllergies(new String[]{"peanut", "nillacilan"});
        when(jSonMedicalRecordService.updateMedicalRecord(johnBoyd)).thenReturn(johnBoyd);

        mockMvc.perform(put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSonRepository.toJsonString(johnBoyd)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    public void updateMedicalRecord_shouldNotUpdate_whenMedicalRecordDoesNotExist() throws Exception {
        String        firstName     = "Kendrick";
        String        lastName      = "Stelzer";
        LocalDate     birthdate     = LocalDate.of(2014, 3, 6);
        String[]      medications   = {"noxidian:100mg", "pharmacol:2500mg"};
        String[]      allergies     = {};
        MedicalRecord medicalRecord = new MedicalRecord(firstName, lastName, birthdate, medications, allergies);

        when(jSonMedicalRecordService.updateMedicalRecord(any(MedicalRecord.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSonRepository.toJsonString(medicalRecord)))
               .andDo(print())
               .andExpect(status().isNotFound());
    }
}
