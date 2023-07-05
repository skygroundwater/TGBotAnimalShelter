package com.telegrambotanimalshelter.services.petservice;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatsServiceImpl implements PetService<Cat> {

    private final CatsRepository catsRepository;

    private final Shelter shelter;

    @Autowired
    public CatsServiceImpl(CatsRepository catsRepository, @Qualifier("catShelter") Shelter shelter) {
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
        if (cat != null) {
            return catsRepository.save(cat);
        } else throw new NotValidDataException("Отправьте информацию снова");
    }

    @Override
    public Cat setPetOwner(Cat cat, PetOwner petOwner) {
        cat.setSheltered(true);
        cat.setPetOwner(petOwner);
        return catsRepository.save(cat);
    }

    @Override
    public void setPhoto(String name, byte[] photo) {
        Cat catsByNickName = catsRepository.findCatsByNickName(name);
        catsByNickName.setPhoto(photo);
        catsRepository.save(catsByNickName);
    }

    @Override
    public byte[] getPhoto(String name) {
        Cat catsByNickName = catsRepository.findCatsByNickName(name);
        return catsByNickName.getPhoto();
    }

    @Override
    public Cat findPetByName(String name) {
        return catsRepository.findCatsByNickName(name);
    }

    @Override
    public List<Cat> findPetsByPetOwner(PetOwner petOwner) {
        return catsRepository.findCatsByPetOwner(petOwner);
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
