package com.safetynet.safetynetalerts.model.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirePersonViewModel {
    private String       lastName;
    private String       phone;
    private int          age;
    private List<String> medications;
    private List<String> allergies;
}
