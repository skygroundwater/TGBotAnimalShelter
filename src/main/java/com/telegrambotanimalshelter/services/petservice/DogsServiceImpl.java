package com.telegrambotanimalshelter.services.petservice;


import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DogsServiceImpl implements PetService<Dog> {

    private final DogsRepository dogsRepository;

    private final Shelter shelter;

    @Autowired
    public DogsServiceImpl(DogsRepository dogsRepository, @Qualifier("dogShelter") Shelter shelter) {
        shelter.getAllAnimalsFromDB(dogsRepository.findAll());
        this.dogsRepository = dogsRepository;
        this.shelter = shelter;
    }

    @Override
    public List<Dog> findPetsByPetOwner(PetOwner petOwner) {
        return dogsRepository.findDogsByPetOwner(petOwner);
    }

    @Override
    public List<Dog> getAllPets(){
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
