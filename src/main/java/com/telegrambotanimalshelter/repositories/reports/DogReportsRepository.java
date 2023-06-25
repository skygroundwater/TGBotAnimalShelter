package com.telegrambotanimalshelter.repositories.reports;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.DogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogReportsRepository extends JpaRepository<DogReport, Long> {

    List<DogReport> findDogReportsByPetOwner(PetOwner petOwner);

    List<DogReport> findDogReportsByDog(Dog dog);

    void deleteDogReportsByDog(Dog dog);



}
