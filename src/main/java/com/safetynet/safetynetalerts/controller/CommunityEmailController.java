package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.service.JSonPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    @Autowired
    private JSonPersonService jSonPersonService;

    @GetMapping
    public Set<String> getCommunityEmail(@RequestParam String city) {
        return jSonPersonService.getCommunityEmail(city);
    }
}
