package com.safetynet.safetynetalerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private String firstName;

    private String lastName;

    private String address;

    private String city;

    private int zip;

    private String phone;

    private String email;

    public boolean equals(Person person) {
        return (this.getFirstName().equalsIgnoreCase(person.getFirstName())
                && this.getLastName().equalsIgnoreCase(person.getLastName()));
    }
}
