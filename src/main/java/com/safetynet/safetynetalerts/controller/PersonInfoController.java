package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.viewmodel.PersonInfoViewModel;
import com.safetynet.safetynetalerts.service.JSonPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    @Autowired
    private JSonPersonService jSonPersonService;

    @GetMapping
    public PersonInfoViewModel getPersonInfo(@RequestParam String firstName, @RequestParam String lastName) {
        return jSonPersonService.getPersonInfo(firstName, lastName);
    }
}
