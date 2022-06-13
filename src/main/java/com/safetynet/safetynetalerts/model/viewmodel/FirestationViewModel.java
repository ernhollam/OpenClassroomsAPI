package com.safetynet.safetynetalerts.model.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirestationViewModel {
    private List<FirestationPersonViewModel> people;
    private int                              nbAdults;
    private int                              nbChildren;
}
