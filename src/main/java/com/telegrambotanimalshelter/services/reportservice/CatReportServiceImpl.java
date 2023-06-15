package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.repositories.reports.CatReportsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatReportServiceImpl implements ReportService<CatReport, Cat> {

    private final CatReportsRepository reportsRepository;

    public CatReportServiceImpl(CatReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    @Override
    public CatReport postReport(CatReport catReport) {
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
