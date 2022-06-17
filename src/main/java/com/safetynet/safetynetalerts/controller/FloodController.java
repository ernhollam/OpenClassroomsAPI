package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.viewmodel.FloodViewModel;
import com.safetynet.safetynetalerts.service.JSonFirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flood")
public class FloodController {
    @Autowired
    private JSonFirestationService jSonFirestationService;

    @GetMapping
    public FloodViewModel getCoveredHouseHold(@RequestParam(name = "station") int stationNumber) {
        return jSonFirestationService.getCoveredHouseholds(stationNumber);
    }
}