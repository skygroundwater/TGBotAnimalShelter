package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.repositories.reports.CatReportsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatReportServiceImplTest {

    @InjectMocks
    CatReportServiceImpl catReportService;

    @Mock
    CatReportsRepository reportsRepository;

    @Mock
    MultipartFile multipartFile;

    CatReport catReport = new CatReport();
    Cat cat = new Cat();
    List<CatReport> catReports = List.of(catReport);

    @Test
    void postReport() {
        when(reportsRepository.save(catReport)).thenReturn(catReport);
        assertEquals(catReportService.postReport(catReport, multipartFile), catReport);
    }

    @Test
    void putReport() {
        when(reportsRepository.save(catReport)).thenReturn(catReport);
        assertEquals(catReportService.putReport(catReport), catReport);
    }

    @Test
    void deleteReportsByPet() {
        assertEquals(catReportService.deleteReportsByPet(cat), HttpStatus.OK);
    }

    @Test
    void getAllReports() {
        when(reportsRepository.findAll()).thenReturn(catReports);
        assertEquals(catReportService.getAllReports(), catReports);
    }

    @Test
    void findReportsFromPet() {
        when(reportsRepository.findCatReportsByCat(cat)).thenReturn(catReports);
        assertEquals(catReportService.findReportsFromPet(cat), catReports);
    }
}