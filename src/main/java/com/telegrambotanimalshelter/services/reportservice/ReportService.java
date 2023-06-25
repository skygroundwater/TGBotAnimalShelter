package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.reports.Report;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService<R extends Report, A extends Animal, I extends AppImage> {

    R postReport(R report, MultipartFile... multipartFiles);

    R putReport(R report);

    HttpStatus deleteReportsByPet(A animal);

    List<R> getAllReports();

    List<R> findReportsFromPet(A animal);

    HttpStatus deleteReport(R report);

}
