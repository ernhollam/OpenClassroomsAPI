package com.safetynet.safetynetalerts.model.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationNumberViewModel {
    private List<StationNumberPersonViewModel> people;
    private int                                nbAdults;
    private int                                nbChildren;
}
