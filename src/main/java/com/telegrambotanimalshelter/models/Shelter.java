package com.telegrambotanimalshelter.models;

import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.animals.Animal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class Shelter {

    private final String name;

    private String description;

    private String workingHours;

    private String safetyPrecautions;

    private String securityContacts;

    private String acquaintance;

    private String contractDocuments;

    private String transportation;

    private String homeForLittle;

    private String homeForAdult;

    private String homeForRestricted;

    private List<? extends Animal> animals;

    private ShelterType shelterType;

    public void getAllAnimalsFromDB(List<? extends Animal> animals) {
        this.animals = animals;
    }
}