package com.telegrambotanimalshelter.services.petservice;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface PetService<T extends Animal> {

    T postPet(T pet);

    HttpStatus deletePet(T pet);

    T findPet(Long id);

    T putPet(T pet);

    List<T> findPetsByPetOwner(PetOwner petOwner);

    List<T> getAllPets();

    Shelter getShelter();

}
