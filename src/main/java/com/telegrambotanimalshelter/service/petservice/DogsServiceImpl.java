package com.telegrambotanimalshelter.service.petservice;


import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.DogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DogsServiceImpl implements PetService {

    private final DogsRepository dogsRepository;

    private final Shelter shelter;

    @Autowired
    public DogsServiceImpl(DogsRepository dogsRepository, @Qualifier("dogShelter") Shelter shelter) {
        shelter.getAllAnimalsFromDB(dogsRepository.findAll());
        this.dogsRepository = dogsRepository;
        this.shelter = shelter;
    }


    @Override
    public String addNewPet(Cat cat) {
        return null;
    }

    @Override
    public Shelter getShelter() {
        return shelter;
    }
}
