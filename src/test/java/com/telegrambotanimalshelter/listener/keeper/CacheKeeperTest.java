package com.telegrambotanimalshelter.listener.keeper;

import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.FileService;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petservice.CatsServiceImpl;
import com.telegrambotanimalshelter.services.petservice.PetService;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CacheKeeperTest<A extends Animal, R extends Report> {

    @Mock
    private PetOwnersService petOwnersService;
    @Mock
    private VolunteerService volunteerService;
    @Mock
    private PetService<Cat> catService;
    @Mock
    private PetService<Dog> dogService;
    @Mock
    private ReportService<CatReport, Cat, CatImage> catReportService;
    @Mock
    private ReportService<DogReport, Dog, DogImage> dogReportService;
    @Mock
    private FileService<? extends AppImage> fileService;
    @InjectMocks
    private CacheKeeper<A, R> keeper;
    private Map<Long, PetOwner> petOwnersById = new HashMap<>();
    private Map<Long, Volunteer> volunteers = new HashMap<>();
    private Map<Long, List<Cat>> catsByPetOwnerId = new HashMap<>();
    private Map<Long, List<Dog>> dogsByPetOwnerId = new HashMap<>();
    private Map<Long, List<CatReport>> catReportsByCatId = new HashMap<>();
    private Map<Long, List<DogReport>> dogReportsByDogId = new HashMap<>();
    private List<R> cashedReports = new ArrayList<>();
    private Map<Long, R> actualReportByPetOwnerId = new HashMap<>();
    private Map<Long, A> actualPetsInReportProcess = new HashMap<>();
    private List<CatImage> catImages = new ArrayList<>();
    private List<DogImage> dogImages = new ArrayList<>();


}