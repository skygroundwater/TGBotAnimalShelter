package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.Image;
import com.telegrambotanimalshelter.models.reports.Report;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService<T extends Report, N extends Animal, I extends Image> {

    T postReport(T report, MultipartFile... multipartFiles);

    T putReport(T report);

    HttpStatus deleteReportsByPet(N animal);

    List<T> getAllReports();

    List<T> findReportsFromPet(N animal);

}
