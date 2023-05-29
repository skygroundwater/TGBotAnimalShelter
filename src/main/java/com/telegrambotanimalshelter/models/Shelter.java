package com.telegrambotanimalshelter.models;

import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.animals.Animal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Shelter {

    private String name;

    private String description;

    private List<? extends Animal> animals;

    private ShelterType shelterType;

    public void getAllAnimalsFromDB(List<? extends Animal> animals) {
        this.animals = animals;
    }
}