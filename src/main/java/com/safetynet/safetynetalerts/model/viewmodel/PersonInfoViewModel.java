package com.safetynet.safetynetalerts.model.viewmodel;

import com.safetynet.safetynetalerts.model.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonInfoViewModel {
    private String       lastName;
    private String       address;
    private int          age;
    private String       email;
    private List<String> medications;
    private List<String> allergies;
    private List<Person> peopleWithSameName;
}
