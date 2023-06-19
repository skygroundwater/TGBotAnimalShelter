package com.telegrambotanimalshelter.services.petservice;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.listener.parts.IntroductionPart;
import com.telegrambotanimalshelter.listener.parts.BecomingPetOwnerPart;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.telegrambotanimalshelter.utils.Constants.callBackQueryConstantCheck;

@Service
public class CatsServiceImpl implements PetService<Cat> {

    private final CatsRepository catsRepository;

    private final IntroductionPart introductionPart;

    private final BecomingPetOwnerPart becomingPetOwnerPart;

    private final Shelter shelter;

    @Autowired
    public CatsServiceImpl(CatsRepository catsRepository,
                           IntroductionPart introductionPart, BecomingPetOwnerPart becomingPetOwnerPart, @Qualifier("catShelter") Shelter shelter) {
        this.introductionPart = introductionPart;
        this.becomingPetOwnerPart = becomingPetOwnerPart;
        shelter.getAllAnimalsFromDB(catsRepository.findAll());
        this.catsRepository = catsRepository;
        this.shelter = shelter;
    }

    @Override
    public Cat postPet(Cat cat) {
        return catsRepository.save(cat);
    }

    @Override
    public HttpStatus deletePet(Cat cat) {
        catsRepository.delete(cat);
        return HttpStatus.OK;
    }

    @Override
    public Cat findPet(Long id) {
        return catsRepository.findById(id)
                .orElseThrow(() -> new NotFoundInDataBaseException("Кот не найден"));
    }

    @Override
    public Cat putPet(Cat cat) {
        if(cat != null){
            return catsRepository.save(cat);
        }
        else throw new NotValidDataException("Отправьте информацию снова");
    }

    @Override
    public List<Cat> findPetsByPetOwner(PetOwner petOwner) {
        return catsRepository.findCatsByPetOwner(petOwner);
    }

    @Override
    public void callBackQueryServiceCheck(CallbackQuery callbackQuery) {
        callBackQueryConstantCheck(callbackQuery, shelter, introductionPart, becomingPetOwnerPart);
    }

    @Override
    public List<Cat> getAllPets() {
        return catsRepository.findAll();
    }

    @Override
    public Shelter getShelter() {
        return shelter;
    }


}
