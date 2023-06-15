package com.telegrambotanimalshelter.services.petownerservice;

import com.telegrambotanimalshelter.models.PetOwner;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface PetOwnersService {
    PetOwner postPetOwner(PetOwner petOwner);

    HttpStatus deletePetOwner(PetOwner petOwner);

    PetOwner findPetOwner(Long id);

    PetOwner putPetOwner(PetOwner petOwner);

    List<PetOwner> findActualPetOwners();

    List<PetOwner> getAllPetOwners();
}
