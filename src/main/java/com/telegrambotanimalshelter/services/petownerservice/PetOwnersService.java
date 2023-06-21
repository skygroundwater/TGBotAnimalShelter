package com.telegrambotanimalshelter.services.petownerservice;

import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface PetOwnersService {
    PetOwner postPetOwner(PetOwner petOwner);

    HttpStatus deletePetOwner(PetOwner petOwner);

    PetOwner findPetOwner(Long id);

    PetOwner putPetOwner(PetOwner petOwner);

    void savePotentialPetOwner(Update update);

    PetOwner setPetOwnerReportRequest(Long id, boolean trueOrFalse);

    boolean checkReportRequestStatus(Long id);

    boolean checkContactRequestStatus(Long id);

    PetOwner setPetOwnerContactRequest(Long id, boolean trueOrFalse);

    PetOwner setPetOwnerToVolunteerChat(Long id, Volunteer volunteer, boolean trueOrFalse);

    boolean checkVolunteerChatStatus(Long id);

    List<PetOwner> findActualPetOwners();

    List<PetOwner> getAllPetOwners();
}
