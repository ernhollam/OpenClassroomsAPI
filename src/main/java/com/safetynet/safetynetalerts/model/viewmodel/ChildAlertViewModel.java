package com.safetynet.safetynetalerts.model.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildAlertViewModel {
    private List<ChildViewModel>           children;
    private List<PersonAtAddressViewModel> otherHouseholdMembers;
}
