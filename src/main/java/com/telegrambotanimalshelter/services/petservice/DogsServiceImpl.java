package com.telegrambotanimalshelter.services.petservice;


import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import com.telegrambotanimalshelter.repositories.images.BinaryContentRepository;
import com.telegrambotanimalshelter.repositories.images.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
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
    public Dog postPet(Dog dog){
        return dogsRepository.save(dog);
    }

    @Override
    public HttpStatus deletePet(Dog dog){
        dogsRepository.delete(dog);
        return HttpStatus.OK;
    }

    @Override
    public Dog findPet(Long id) {
        return dogsRepository.findById(id).orElseThrow(() -> new NotFoundInDataBaseException("Пёс не найден"));
    }

    @Override
    public Dog putPet(Dog dog) {
        if(dog != null){
            return dogsRepository.save(dog);
        }
        else throw new NotValidDataException("Отправьте информацию снова");
    }


    @Override
    public List<Dog> findPetsByPetOwner(PetOwner petOwner) {
        return dogsRepository.findDogsByPetOwner(petOwner);
    }


    @Override
    public List<Dog> getAllPets() {
        return dogsRepository.findAll();
    }

    @Override
    public Shelter getShelter() {
        return shelter;
    }
}
