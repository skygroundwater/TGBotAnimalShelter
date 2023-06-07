package com.telegrambotanimalshelter.services.petservice;


import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatsServiceImpl implements PetService<Cat> {

    private final CatsRepository catsRepository;

    private final Shelter shelter;

    @Autowired
    public CatsServiceImpl(CatsRepository catsRepository,
                           @Qualifier("catShelter") Shelter shelter) {
        shelter.getAllAnimalsFromDB(catsRepository.findAll());
        this.catsRepository = catsRepository;
        this.shelter = shelter;
    }

    @Override
    public List<Cat> findPetsByPetOwner(PetOwner petOwner){
        return catsRepository.findCatsByPetOwner(petOwner);
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
