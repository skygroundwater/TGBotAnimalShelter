package com.telegrambotanimalshelter.repositories;

import com.telegrambotanimalshelter.models.animals.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogsRepository extends JpaRepository<Dog, Long> {
}
