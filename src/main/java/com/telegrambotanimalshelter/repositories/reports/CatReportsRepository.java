package com.telegrambotanimalshelter.repositories.reports;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.CatReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatReportsRepository extends JpaRepository<CatReport, Long> {

    List<CatReport> findCatReportsByPetOwner(PetOwner petOwner);

    List<CatReport> findCatReportsByCat(Cat cat);

    void deleteCatReportsByCat(Cat cat);


}
