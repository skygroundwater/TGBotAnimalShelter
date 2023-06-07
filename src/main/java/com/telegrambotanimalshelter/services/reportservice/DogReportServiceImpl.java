package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.repositories.reports.DogReportsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DogReportServiceImpl implements ReportService<DogReport, Dog> {

    private final DogReportsRepository reportsRepository;

    public DogReportServiceImpl(DogReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    @Override
    public List<DogReport> getAllReports(){
        return reportsRepository.findAll();
    }

    @Override
    public List<DogReport> findReportsFromPet(Dog dog){
        return reportsRepository.findDogReportsByDog(dog);
    }

}
