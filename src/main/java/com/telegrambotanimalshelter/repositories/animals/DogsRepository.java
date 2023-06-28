package com.telegrambotanimalshelter.repositories.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogsRepository extends JpaRepository<Dog, Long> {

    List<Dog> findDogsByPetOwner(PetOwner petOwner);

    List<Dog> findDogsBySheltered(boolean isSheltered);
    Dog findDogsByNickName(String name);
}
