package com.telegrambotanimalshelter.services.petownerservice;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.repositories.PetOwnersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetOwnersServiceImpl implements PetOwnersService {

    private final PetOwnersRepository petOwnersRepository;

    @Autowired
    public PetOwnersServiceImpl(PetOwnersRepository petOwnersRepository) {
        this.petOwnersRepository = petOwnersRepository;
    }

    @Override
    public PetOwner savePetOwnerToDB(PetOwner petOwner) {
        if (petOwner == null) throw new NotValidDataException("Не валидные данные");
        return petOwnersRepository.save(petOwner);
    }

    @Override
    public PetOwner findPetOwnerById(Long id) {
        return petOwnersRepository.findById(id).orElseThrow(() ->
                new NotFoundInDataBaseException("Пользователь не был найден в базе данных"));
    }

    @Override
    public List<PetOwner> findActualPetOwners(){
        return petOwnersRepository.findPetOwnersByHasPetsTrue();
    }

    @Override
    public List<PetOwner> getAllPetOwners(){
        return petOwnersRepository.findAll();
    }
}
