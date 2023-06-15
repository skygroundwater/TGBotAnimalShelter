package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface ReportService<T extends Report, N extends Animal> {

    T postReport(T report);

    T putReport(T report);

    HttpStatus deleteReportsByPet(N animal);

    List<T> getAllReports();

    List<T> findReportsFromPet(N animal);
}
