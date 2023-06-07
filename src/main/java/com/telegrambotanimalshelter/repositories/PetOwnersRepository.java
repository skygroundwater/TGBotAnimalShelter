package com.telegrambotanimalshelter.repositories;

import com.telegrambotanimalshelter.models.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetOwnersRepository extends JpaRepository<PetOwner, Long> {

    List<PetOwner> findPetOwnersByHasPetsTrue();

}
