package com.telegrambotanimalshelter.services.reportservice;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.repositories.reports.CatReportsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CatReportServiceImplTest {

    @InjectMocks
    private CatReportServiceImpl catReportService;

    @Mock
    private CatReportsRepository reportsRepository;

    @Mock
    private Cat cat;

    private final List<CatReport> catReports = new ArrayList<>();

    @Test
    void shouldGetAllReports() {
        when(catReportService.getAllReports()).thenReturn(catReports);
        assertEquals(reportsRepository.findAll(), catReports);
    }

    @Test
    void shouldFindReportsFromPet() {
        when(catReportService.findReportsFromPet(cat)).thenReturn(catReports);
        assertEquals(reportsRepository.findCatReportsByCat(cat), catReports);
    }
}