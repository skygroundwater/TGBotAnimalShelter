package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;

import java.util.List;

public interface ReportService<T extends Report, N extends Animal> {

    List<T> getAllReports();

    List<T> findReportsFromPet(N animal);
}
