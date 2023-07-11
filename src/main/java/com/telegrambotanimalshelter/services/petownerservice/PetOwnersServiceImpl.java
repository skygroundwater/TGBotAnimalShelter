package com.telegrambotanimalshelter.services.petownerservice;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.repositories.PetOwnersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PetOwnersServiceImpl implements PetOwnersService {

    private final PetOwnersRepository petOwnersRepository;

    @Autowired
    public PetOwnersServiceImpl(PetOwnersRepository petOwnersRepository) {
        this.petOwnersRepository = petOwnersRepository;
    }

    @Override
    public PetOwner postPetOwner(PetOwner petOwner) {
        if (petOwner == null) throw new NotValidDataException("Не валидные данные");
        return petOwnersRepository.save(petOwner);
    }

    @Override
    public HttpStatus deletePetOwner(PetOwner petOwner) {
        petOwnersRepository.delete(petOwner);
        return HttpStatus.OK;
    }

    @Override
    public PetOwner findPetOwner(Long id) {
        return petOwnersRepository.findById(id).orElseThrow(() ->
                new NotFoundInDataBaseException("Пользователь не был найден в базе данных"));
    }

    @Override
    public PetOwner putPetOwner(PetOwner petOwner) {
        return petOwnersRepository.save(petOwner);
    }

    @Override
    public PetOwner savePotentialPetOwner(Update update) {
        Message message = update.message();
        Chat chat = message.chat();
        try {
            return findPetOwner(chat.id());
        } catch (NotFoundInDataBaseException e) {
            return postPetOwner(new PetOwner(chat.id(), chat.firstName(), chat.lastName(),
                    chat.username(), LocalDateTime.now(), false));
        }
    }

    @Override
    public PetOwner setChoosingPets(Long id, boolean trueOrFalse){
        PetOwner petOwner = findPetOwner(id);
        petOwner.setChoosingPet(trueOrFalse);
        return putPetOwner(petOwner);
    }

    @Override
    public PetOwner setLookingAboutPet(Long id, boolean trueOrFalse){
        PetOwner petOwner = findPetOwner(id);
        petOwner.setLookingAboutPet(true);
        return petOwner;
    }

    @Override
    public PetOwner setPetOwnerReportRequest(Long id, boolean trueOrFalse) {
        PetOwner petOwner = findPetOwner(id);
        petOwner.setReportRequest(trueOrFalse);
        return putPetOwner(petOwner);
    }

    @Override
    public boolean checkReportRequestStatus(Long id) {
        try {
            return findPetOwner(id).isReportRequest();
        } catch (NotFoundInDataBaseException e) {
            return false;
        }
    }

    @Override
    public boolean checkContactRequestStatus(Long id) {
        try {
            return findPetOwner(id).isContactRequest();
        } catch (NotFoundInDataBaseException e) {
            return false;
        }
    }

    @Override
    public PetOwner setPetOwnerContactRequest(Long id, boolean trueOrFalse) {
        PetOwner petOwner = findPetOwner(id);
        petOwner.setContactRequest(trueOrFalse);
        return putPetOwner(petOwner);
    }

    @Override
    public PetOwner setPetOwnerToVolunteerChat(Long id, Volunteer volunteer, boolean trueOrFalse) {
        PetOwner petOwner = findPetOwner(id);
        petOwner.setVolunteerChat(trueOrFalse);
        petOwner.setVolunteer(volunteer);
        putPetOwner(petOwner);
        return petOwner;
    }

    @Override
    public boolean checkVolunteerChatStatus(Long id) {
        try {
            return findPetOwner(id).isVolunteerChat();
        } catch (NotFoundInDataBaseException e) {
            return false;
        }
    }

    @Override
    public List<PetOwner> findActualPetOwners() {
        return petOwnersRepository.findPetOwnersByHasPetsTrue();
    }

    @Override
    public List<PetOwner> getAllPetOwners() {
        return petOwnersRepository.findAll();
    }
}
