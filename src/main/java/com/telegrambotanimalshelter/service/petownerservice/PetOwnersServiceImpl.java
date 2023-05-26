package com.telegrambotanimalshelter.service.petownerservice;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.repositories.PetOwnersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetOwnersServiceImpl implements PetOwnersService{

    private final PetOwnersRepository petOwnersRepository;

    @Autowired
    public PetOwnersServiceImpl(PetOwnersRepository petOwnersRepository) {
        this.petOwnersRepository = petOwnersRepository;
    }

    @Override
    public String addNewPetOwnerToDB(PetOwner petOwner){
        if(petOwner != null) {
            petOwnersRepository.save(petOwner);
            return "Новый потенциальный владелец добавлен в базу";
        }
        return "Новый владелец не добавлен";
    }


}
