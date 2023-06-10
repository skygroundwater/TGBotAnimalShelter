package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.repositories.reports.DogReportsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DogReportServiceImplTest {

    @InjectMocks
    private DogReportServiceImpl dogReportService;

    @Mock
    private DogReportsRepository reportsRepository;

    @Mock
    private Dog dog;

    private final List<DogReport> dogReports = new ArrayList<>();

    @Test
    void shouldGetAllReports() {
        when(dogReportService.getAllReports()).thenReturn(dogReports);
        assertEquals(reportsRepository.findAll(), dogReports);
    }

    @Test
    void shouldFindReportsFromPet() {
        when(dogReportService.findReportsFromPet(dog)).thenReturn(dogReports);
        assertEquals(reportsRepository.findDogReportsByDog(dog), dogReports);
    }
}