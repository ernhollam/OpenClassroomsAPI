package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.JSonFirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firestation")
public class FirestationController {

    @Autowired
    private JSonFirestationService jSonFirestationService;


    @GetMapping
    public List<Firestation> getFirestations() {
        return jSonFirestationService.getFirestations();
    }

    @GetMapping("/{stationNumber}")
    public Firestation getFirestation(@PathVariable int stationNumber) {
        return jSonFirestationService.getFirestation(stationNumber).
                                     orElseThrow(() -> new ResourceNotFoundException("Firestation n°" + stationNumber +
                                                                                     " was not found."));
    }


    @PostMapping
    public Firestation createFirestation(@RequestBody Firestation firestation) throws Exception {
        return jSonFirestationService.saveFirestation(firestation);
    }


    @PutMapping
    public Firestation updateFirestation(@RequestBody Firestation firestation) throws Exception {
        return jSonFirestationService.updateFirestation(firestation);
    }


    @DeleteMapping("/{stationNumber}")
    public void deleteFirestation(@PathVariable int stationNumber) throws Exception {
        jSonFirestationService.deleteFirestation(stationNumber);
    }
}
