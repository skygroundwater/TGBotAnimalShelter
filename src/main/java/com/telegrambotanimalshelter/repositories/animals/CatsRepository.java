package com.telegrambotanimalshelter.repositories.animals;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatsRepository extends JpaRepository<Cat, Long> {

    List<Cat> findCatsByPetOwner(PetOwner petOwner);
    List<Cat> findCatsBySheltered(boolean isSheltered);
    Cat findCatsByNickName(String name);

}
