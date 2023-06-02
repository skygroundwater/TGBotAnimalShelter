package com.telegrambotanimalshelter.service.petservice;

import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;

public interface PetService {
    String addNewPet(Cat cat);

    Shelter getShelter();
}
