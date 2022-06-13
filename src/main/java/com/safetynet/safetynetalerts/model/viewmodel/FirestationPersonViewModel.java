package com.safetynet.safetynetalerts.model.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirestationPersonViewModel {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
