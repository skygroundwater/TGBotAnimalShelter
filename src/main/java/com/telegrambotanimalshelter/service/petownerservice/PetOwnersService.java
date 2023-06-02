package com.telegrambotanimalshelter.service.petownerservice;

import com.telegrambotanimalshelter.models.PetOwner;

public interface PetOwnersService {
    String addNewPetOwnerToDB(PetOwner petOwner);

    PetOwner findPetOwnerById(Long id);
}
