package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.JSonFirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/firestations")
public class FirestationsController {

    @Autowired
    private JSonFirestationService jSonFirestationService;

    @GetMapping
    public List<Firestation> getFirestations() {
        return jSonFirestationService.getFirestations();
    }
}
