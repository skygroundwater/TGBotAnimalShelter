package com.telegrambotanimalshelter.listener.parts.keeper;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
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
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.telegrambotanimalshelter.utils.ConstantsForTesting.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @Mock
    private Cache<A, R> cache;
    @InjectMocks
    private CacheKeeper<A, R> out;

    @BeforeEach
    void setUp() {
        //given
        out = new CacheKeeper<>(
                petOwnersService,
                volunteerService,
                catService,
                dogService,
                dogReportService,
                catReportService,
                fileService,
                cache);

        //given
        when(petOwnersService.getAllPetOwners())
                .thenReturn(List.of(petOwner1, petOwner2));

        when(cache.getCatsByPetOwnerId())
                .thenReturn(cats);

        when(cache.getVolunteers())
                .thenReturn(volunteers);

        when(cache.getDogsByPetOwnerId())
                .thenReturn(dogs);

        when(catReportService.getAllReports())
                .thenReturn(catReports);

        when(dogReportService.getAllReports())
                .thenReturn(dogReports);

        when(cache.getPetOwnersById())
                .thenReturn(petOwners);

        when(cache.getCashedReports())
                .thenReturn((List<R>) reports);
    }

    @Test
    public void fillPetOwnersCache_ReturnsStringWithInfo() {
        //when
        String info = out.fillPetOwnersCache();
        //then
        assertEquals(info, "Кеш заполнен усыновителями и их животными");
    }

    @Test
    public void fillVolunteersCache_ReturnsStringWithInfo() {
        //when
        String info = out.fillVolunteersCache();
        //then
        assertEquals(info, "Кеш заполнен волонтерами");
    }

    @Test
    public void fillReportsCache() {
        //when
        String info = out.fillReportsCache();
        //then
        assertEquals(info, "Кеш заполнен отчетами");
    }

    @Test
    public void fillImagesCache_ReturnsStringWithInfo() {
        //when
        String info = out.fillImagesCache();
        //then
        assertEquals(info, "Кеш заполнен фотографиями животных из базы данных");
    }

    @Test
    public void getCatsByPetOwnerIdFromCache_ReturnsListWithCats() {
        //when
        List<Cat> testingCats =
                out.getCatsByPetOwnerIdFromCache(
                        petOwner1.getId());
        //then
        assertEquals(testingCats, cats.get(
                petOwner1.getId()));
    }

    @Test
    public void getDogsByPetOwnerIdFromCache_ReturnsListWithDogs() {
        //when
        List<Dog> testingDogs =
                out.getDogByPetOwnerIdFromCache(
                        petOwner2.getId());
        //then
        assertEquals(testingDogs, dogs.get(
                petOwner2.getId()));
    }

    @Test
    public void setAllAnimalsReportedToFalse_ReturnsStringWithInfo() {
        //given
        String info = "У всех животных в базе данных и кеше обновлен статус об отчете";
        when(cache.getCatsByPetOwnerId()).thenReturn(cats);
        when(cache.getDogsByPetOwnerId()).thenReturn(dogs);
        //when
        String testingInfo = out.setAllAnimalsReportedToFalse();
        //then
        assertEquals(info, testingInfo);
    }

    @Test
    public void appointVolunteerToCheckReports_ReturnsCheckingReportFromOfficeVolunteer() {
        //given
        Volunteer testingVolunteer = volunteer1;
        testingVolunteer.setFree(false);
        testingVolunteer.setInOffice(true);
        testingVolunteer.setCheckingReports(true);
        //when
        Volunteer volunteer =
                out.appointVolunteerToCheckReports(volunteer1.getId());
        //then
        assertEquals(volunteer, testingVolunteer);
    }

    @Test
    public void volunteerAcceptReport_ReturnsVolunteerWhoIsNotFreeAndInOffice() {
        //given
        Volunteer comparingVolunteer = volunteer1;
        comparingVolunteer.setInOffice(true);
        comparingVolunteer.setFree(false);
        //when
        Volunteer testingVolunteer = out.volunteerAcceptReport(volunteer1.getId(), (R) dogReport);
        //then
        assertEquals(testingVolunteer, comparingVolunteer);
        assertFalse(comparingVolunteer.isCheckingReports());
        assertEquals(comparingVolunteer.getFirstName(), testingVolunteer.getFirstName());
    }

    @Test
    public void volunteerRejectReport_ReturnsVolunteerWhoIsNotFreeAndIsInOffice(){
        //given
        Volunteer comparingVolunteer = volunteer1;
        comparingVolunteer.setInOffice(true);
        comparingVolunteer.setFree(false);
        //when
        Volunteer testingVolunteer =
                out.volunteerRejectReport(
                        volunteer1.getId(), (R) dogReport);
        //then
        assertEquals(testingVolunteer, comparingVolunteer);
    }

    @Test
    public void volunteerWantsToGetOutFromOffice_ReturnsVolunteerWhoIsFreeAndIsNotInOffice(){
        //when
        Volunteer testingVolunteer =
                out.volunteerWantsToGetOutFromOffice(
                        volunteer1.getId());
        //then
        assertEquals(testingVolunteer, volunteer1);
    }

    @Test
    public void createReportFromPetOwner_ReturnsPetOwnerWhoCreatedReport(){
        PetOwner petOwner1 = new PetOwner(
                2L, "Карапет",
                "Карапетов", "karapet",
                LocalDateTime.now(),false
        );

        PetOwner petOwner2 = new PetOwner(
                1L, "Пузанок",
                "Пузанов", "puzanok",
                LocalDateTime.now(), false
        );

        Map<Long, PetOwner> petOwners =
                new HashMap<>(Map.of(
                        petOwner1.getId(), petOwner1,
                        petOwner2.getId(), petOwner2));
        when(cache.getPetOwnersById()).thenReturn(petOwners);
        //when
        PetOwner testingPetOwner = out.createReportForAnimal(petOwner1.getId(), (A) dog1);
        //then
        assertEquals(petOwner1, testingPetOwner);
    }

    @Test
    public void findFreeVolunteer_ReturnsVolunteerWhoIsFree(){
        //given
        Volunteer volunteer1 =
                new Volunteer(
                        1L, "@afrodita",
                        "Афродита", "Боговиева",
                        "afrodita", true,
                        false,false,null);

        Volunteer volunteer2 =
                new Volunteer(
                        2L, "@sohoncev",
                        "Владимир", "Сохонцев",
                        "sohonets", true,
                        false,false,null);

        Map<Long, Volunteer> volunteers =
                new HashMap<>(Map.of(
                        volunteer1.getId(), volunteer1,
                        volunteer2.getId(), volunteer2));
        when(this.cache.getVolunteers())
                .thenReturn(volunteers);
        //when
        Volunteer volunteer = out.findFreeVolunteer();
        //then
        assertTrue(volunteer.isFree());
        assertFalse(volunteer.isInOffice());
        assertFalse(volunteer.isCheckingReports());
    }

    @Test
    public void findFreeVolunteer_ThrowsException(){
        //given
        when(cache.getVolunteers()).thenReturn(Collections.EMPTY_MAP);

        //then
        assertThrows(NotFoundInDataBaseException.class, () -> {
            Volunteer volunteer = out.findFreeVolunteer();
        });
    }
}
