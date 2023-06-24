package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.repositories.images.CatImagesRepository;
import com.telegrambotanimalshelter.repositories.reports.CatReportsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CatReportServiceImpl implements ReportService<CatReport, Cat, CatImage> {

    private final CatReportsRepository reportsRepository;


    private final CatImagesRepository catImagesRepository;

    public CatReportServiceImpl(CatReportsRepository reportsRepository,
                                CatImagesRepository catImagesRepository) {
        this.reportsRepository = reportsRepository;
        this.catImagesRepository = catImagesRepository;
    }

    @Override
    public CatReport postReport(CatReport catReport, MultipartFile... multipartFiles) {
        List<CatImage> images = null;
        if (multipartFiles.length > 0) {


        }

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