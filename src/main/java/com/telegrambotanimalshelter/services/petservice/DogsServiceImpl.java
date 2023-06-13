package com.telegrambotanimalshelter.services.petservice;


import com.pengrad.telegrambot.model.CallbackQuery;
import com.telegrambotanimalshelter.listener.parts.Part1;
import com.telegrambotanimalshelter.listener.parts.Part2;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import com.telegrambotanimalshelter.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DogsServiceImpl implements PetService<Dog> {

    private final DogsRepository dogsRepository;

    private final Shelter shelter;

    private final Part1 part1;

    private final Part2 part2;

    @Autowired
    public DogsServiceImpl(DogsRepository dogsRepository, @Qualifier("dogShelter") Shelter shelter, Part1 part1, Part2 part2) {
        this.part1 = part1;
        this.part2 = part2;
        shelter.getAllAnimalsFromDB(dogsRepository.findAll());
        this.dogsRepository = dogsRepository;
        this.shelter = shelter;
    }

    @Override
    public List<Dog> findPetsByPetOwner(PetOwner petOwner) {
        return dogsRepository.findDogsByPetOwner(petOwner);
    }

    @Override
    public void callBackQueryServiceCheck(CallbackQuery callbackQuery) {
        Constants.callBackQueryConstantCheck(callbackQuery, shelter, part1, part2);
        if ("first_meeting".equals(callbackQuery.data())){
            part2.firstMeetingWithDog(callbackQuery.from().id(), shelter);
        }
    }

    @Override
    public List<Dog> getAllPets() {
        return dogsRepository.findAll();
    }

    @Override
    public Dog savePet(Dog dog) {
        return dogsRepository.save(dog);
    }

    @Override
    public Shelter getShelter() {
        return shelter;
    }
}
