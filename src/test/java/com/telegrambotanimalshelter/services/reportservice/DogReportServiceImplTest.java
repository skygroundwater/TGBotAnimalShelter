package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.repositories.reports.DogReportsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogReportServiceImplTest {

    @InjectMocks
    DogReportServiceImpl dogReportService;

    @Mock
    DogReportsRepository reportsRepository;

    @Mock
    MultipartFile multipartFile;

    DogReport dogReport = new DogReport();
    Dog dog = new Dog();
    List<DogReport> dogReports = List.of(dogReport);

    @Test
    void postReport() {
        when(reportsRepository.save(dogReport)).thenReturn(dogReport);
        assertEquals(dogReportService.postReport(dogReport, multipartFile), dogReport);
    }

    @Test
    void putReport() {
        when(reportsRepository.save(dogReport)).thenReturn(dogReport);
        assertEquals(dogReportService.putReport(dogReport), dogReport);
    }

    @Test
    void deleteReportsByPet() {
        assertEquals(dogReportService.deleteReportsByPet(dog), HttpStatus.OK);
    }

    @Test
    void getAllReports() {
        when(reportsRepository.findAll()).thenReturn(dogReports);
        assertEquals(dogReportService.getAllReports(), dogReports);
    }

    @Test
    void findReportsFromPet() {
        when(reportsRepository.findDogReportsByDog(dog)).thenReturn(dogReports);
        assertEquals(dogReportService.findReportsFromPet(dog), dogReports);
    }

    @Test
    void deleteReport() {

    }
}