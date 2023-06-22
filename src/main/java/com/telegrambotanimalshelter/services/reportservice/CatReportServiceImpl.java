package com.telegrambotanimalshelter.services.reportservice;

import com.pengrad.telegrambot.model.PhotoSize;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.repositories.images.BinaryContentRepository;
import com.telegrambotanimalshelter.repositories.images.CatImagesRepository;
import com.telegrambotanimalshelter.repositories.images.DocumentRepository;
import com.telegrambotanimalshelter.repositories.reports.CatReportsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CatReportServiceImpl implements ReportService<CatReport, Cat, CatImage> {

    private final CatReportsRepository reportsRepository;
    private final CatImagesRepository<CatReport> catImagesRepository;

    public CatReportServiceImpl(CatReportsRepository reportsRepository,
                                BinaryContentRepository binaryContentRepository, DocumentRepository documentRepository, CatImagesRepository<CatReport> catImagesRepository) {
        this.reportsRepository = reportsRepository;
        this.catImagesRepository = catImagesRepository;
    }

    @Override
    public CatReport postReport(CatReport catReport, MultipartFile... multipartFiles) {
        List<CatImage> images = null;
        if (multipartFiles.length > 0) {



            catImagesRepository.saveAll(images);
        }
        catReport.setImages(images);
        return reportsRepository.save(catReport);
    }

    @Override
    public CatReport putReport(CatReport catReport) {
        return reportsRepository.save(catReport);
    }

    @Override
    public HttpStatus deleteReportsByPet(Cat cat) {
        reportsRepository.deleteCatReportsByCat(cat);
        return HttpStatus.OK;
    }

    @Override
    public List<CatReport> getAllReports() {
        return reportsRepository.findAll();
    }

    @Override
    public List<CatReport> findReportsFromPet(Cat cat) {
        return reportsRepository.findCatReportsByCat(cat);
    }
}