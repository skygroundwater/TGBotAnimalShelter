package com.telegrambotanimalshelter.services.petownerservice;

import com.telegrambotanimalshelter.models.PetOwner;

import java.util.List;

public interface PetOwnersService {
    PetOwner savePetOwnerToDB(PetOwner petOwner);

    PetOwner findPetOwnerById(Long id);

    List<PetOwner> findActualPetOwners();

    List<PetOwner> getAllPetOwners();
}
