package com.safetynet.safetynetalerts.model.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonInfoViewModel {
    private String   lastName;
    private String   address;
    private int      age;
    private String   email;
    private String[] medications;
    private String[] allergies;
}
