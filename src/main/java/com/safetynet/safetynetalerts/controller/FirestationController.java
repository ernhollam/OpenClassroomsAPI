package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationViewModel;
import com.safetynet.safetynetalerts.service.JSonFirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firestation")
public class FirestationController {

    @Autowired
    private JSonFirestationService jSonFirestationService;


    @GetMapping("s")
    public List<Firestation> getFirestations() {
        return jSonFirestationService.getFirestations();
    }

    @GetMapping("/{stationNumber}")
    public List<Firestation> getFirestation(@PathVariable int stationNumber) {
        return jSonFirestationService.getFirestation(stationNumber);
    }

    @GetMapping
    public FirestationViewModel getPeopleCoveredByStation(@RequestParam int stationNumber) {
        return jSonFirestationService.getPeopleCoveredByStation(stationNumber);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Firestation createFirestation(@RequestBody Firestation firestation) throws Exception {
        return jSonFirestationService.saveFirestation(firestation);
    }

    @PutMapping
    public Firestation updateFirestation(@RequestBody Firestation firestation) throws Exception {
        return jSonFirestationService.updateFirestation(firestation);
    }

    @DeleteMapping("/{stationNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFirestation(@PathVariable int stationNumber) throws Exception {
        jSonFirestationService.deleteFirestation(stationNumber);
    }
}
