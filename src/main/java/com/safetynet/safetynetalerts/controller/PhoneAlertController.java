package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.service.JSonFirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/phoneAlert")
public class PhoneAlertController {
    @Autowired
    private JSonFirestationService jSonFirestationService;

    @GetMapping
    public List<String> getPhoneAlert(@RequestParam(name = "firestation") int stationNumber) {
        return jSonFirestationService.getPhoneAlert(stationNumber);
    }
}
