package com.telegrambotanimalshelter.service.petservice;


import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.CatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CatsServiceImpl implements PetService {

    private final CatsRepository catsRepository;

    private final Shelter shelter;

    @Autowired
    public CatsServiceImpl(CatsRepository catsRepository, @Qualifier("catShelter") Shelter shelter) {
        shelter.getAllAnimalsFromDB(catsRepository.findAll());
        this.catsRepository = catsRepository;
        this.shelter = shelter;
    }

    @Override
    public String addNewPet(Cat cat){
        catsRepository.save(cat);
        return "Кот добавлен";
    }

    @Override
    public Shelter getShelter(){
        return shelter;
    }


}
