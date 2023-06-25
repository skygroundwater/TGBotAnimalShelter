package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.repositories.images.DogImagesRepository;
import com.telegrambotanimalshelter.repositories.reports.DogReportsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DogReportServiceImpl implements ReportService<DogReport, Dog, DogImage> {

    private final DogReportsRepository reportsRepository;

    private final DogImagesRepository dogImagesRepository;

    public DogReportServiceImpl(DogReportsRepository reportsRepository, DogImagesRepository dogImagesRepository) {
        this.reportsRepository = reportsRepository;
        this.dogImagesRepository = dogImagesRepository;
    }

    @Override
    public DogReport postReport(DogReport dogReport, MultipartFile... multipartFiles) {

        reportsRepository.save(dogReport);
        return reportsRepository.save(dogReport);
    }


    @Override
    public DogReport putReport(DogReport dogReport) {
        return reportsRepository.save(dogReport);
    }

    @Override
    public HttpStatus deleteReportsByPet(Dog dog) {
        reportsRepository.deleteDogReportsByDog(dog);
        return HttpStatus.OK;
    }

    @Override
    public List<DogReport> getAllReports() {
        return reportsRepository.findAll();
    }

    @Override
    public List<DogReport> findReportsFromPet(Dog dog) {
        return reportsRepository.findDogReportsByDog(dog);
    }

    @Override
    public HttpStatus deleteReport(DogReport report) {
        reportsRepository.delete(report);
        return HttpStatus.OK;
    }
}