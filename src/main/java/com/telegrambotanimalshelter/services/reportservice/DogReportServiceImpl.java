package com.telegrambotanimalshelter.services.reportservice;

import com.pengrad.telegrambot.model.PhotoSize;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.repositories.images.DogImagesRepository;
import com.telegrambotanimalshelter.repositories.reports.DogReportsRepository;
import com.telegrambotanimalshelter.services.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DogReportServiceImpl implements ReportService<DogReport, Dog, DogImage> {

    private final DogReportsRepository reportsRepository;
    private final DogImagesRepository dogImagesRepository;

    private final FileService fileService;

    public DogReportServiceImpl(DogReportsRepository reportsRepository,
                                DogImagesRepository dogImagesRepository,
                                FileService fileService) {
        this.reportsRepository = reportsRepository;
        this.dogImagesRepository = dogImagesRepository;
        this.fileService = fileService;
    }

    @Override
    public DogReport postReport(DogReport dogReport, MultipartFile... multipartFiles) {
        List<DogImage> images = null;
        if (multipartFiles.length > 0) {


            dogImagesRepository.saveAll(images);
        }
        dogReport.setImages(images);
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
}