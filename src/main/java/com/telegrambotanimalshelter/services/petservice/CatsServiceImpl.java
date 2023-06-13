package com.telegrambotanimalshelter.services.petservice;


import com.pengrad.telegrambot.model.CallbackQuery;
import com.telegrambotanimalshelter.listener.parts.Part1;
import com.telegrambotanimalshelter.listener.parts.Part2;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.telegrambotanimalshelter.utils.Constants.callBackQueryConstantCheck;

@Service
public class CatsServiceImpl implements PetService<Cat> {

    private final CatsRepository catsRepository;

    private final Part1 part1;

    private final Part2 part2;

    private final Shelter shelter;

    @Autowired
    public CatsServiceImpl(CatsRepository catsRepository,
                           Part1 part1, Part2 part2, @Qualifier("catShelter") Shelter shelter) {
        this.part1 = part1;
        this.part2 = part2;
        shelter.getAllAnimalsFromDB(catsRepository.findAll());
        this.catsRepository = catsRepository;
        this.shelter = shelter;
    }

    @Override
    public List<Cat> findPetsByPetOwner(PetOwner petOwner) {
        return catsRepository.findCatsByPetOwner(petOwner);
    }

    @Override
    public void callBackQueryServiceCheck(CallbackQuery callbackQuery) {
        callBackQueryConstantCheck(callbackQuery, shelter, part1, part2);
    }

    @Override
    public List<Cat> getAllPets() {
        return catsRepository.findAll();
    }

    @Override
    public Cat savePet(Cat cat) {
        return catsRepository.save(cat);
    }

    @Override
    public Shelter getShelter() {
        return shelter;
    }


}
