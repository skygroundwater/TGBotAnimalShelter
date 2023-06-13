package com.telegrambotanimalshelter.services.petservice;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetService <T extends Animal>{

    List<T> findPetsByPetOwner(PetOwner petOwner);

    void callBackQueryServiceCheck(CallbackQuery callbackQuery);

    List<T> getAllPets();

    T savePet(T pet);

    Shelter getShelter();
}
