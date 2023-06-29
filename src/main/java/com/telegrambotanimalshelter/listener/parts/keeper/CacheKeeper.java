package com.telegrambotanimalshelter.listener.parts.keeper;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
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
import com.telegrambotanimalshelter.services.petservice.PetService;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

@Component
@Data
public class CacheKeeper<A extends Animal, R extends Report> {

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private final PetService<Cat> catService;

    private final PetService<Dog> dogService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private final FileService<? extends AppImage> fileService;

    private Map<Long, PetOwner> petOwnersById;

    private Map<Long, Volunteer> volunteers;

    private Map<Long, List<Cat>> catsByPetOwnerId;

    private Map<Long, List<Dog>> dogsByPetOwnerId;

    private Map<Long, List<CatReport>> catReportsByCatId;

    private Map<Long, List<DogReport>> dogReportsByDogId;

    private List<R> cashedReports;

    private Map<Long, R> actualReportByPetOwnerId;

    private Map<Long, A> actualPetsInReportProcess;

    private List<CatImage> catImages;

    private List<DogImage> dogImages;

    public CacheKeeper(PetOwnersService petOwnersService,
                       VolunteerService volunteerService,
                       PetService<Cat> catService,
                       PetService<Dog> dogService,
                       @Qualifier("dogReportServiceImpl") ReportService<DogReport, Dog, DogImage> dogReportService,
                       @Qualifier("catReportServiceImpl") ReportService<CatReport, Cat, CatImage> catReportService,
                       FileService<? extends AppImage> fileService) {
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.catService = catService;
        this.dogService = dogService;
        this.catReportService = catReportService;
        this.dogReportService = dogReportService;
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        this.volunteers = new HashMap<>();
        this.petOwnersById = new HashMap<>();
        this.catsByPetOwnerId = new HashMap<>();
        this.dogsByPetOwnerId = new HashMap<>();
        this.catReportsByCatId = new HashMap<>();
        this.dogReportsByDogId = new HashMap<>();
        this.actualReportByPetOwnerId = new HashMap<>();
        this.actualPetsInReportProcess = new HashMap<>();
        this.cashedReports = new ArrayList<>();
        this.catImages = new ArrayList<>();
        this.dogImages = new ArrayList<>();
        fill();
    }

    public void fill() {
        for (PetOwner petOwner : petOwnersService.getAllPetOwners()) {
            petOwnersById.put(petOwner.getId(), petOwner);
            catsByPetOwnerId.put(petOwner.getId(), catService.findPetsByPetOwner(petOwner));
            dogsByPetOwnerId.put(petOwner.getId(), dogService.findPetsByPetOwner(petOwner));
        }
        for (Volunteer volunteer : volunteerService.gatAllVolunteers()) {
            volunteers.put(volunteer.getId(), volunteer);
        }
        for (CatReport catReport : catReportService.getAllReports()) {
            cashedReports.add((R) catReport);
        }
        for (DogReport dogReport : dogReportService.getAllReports()) {
            cashedReports.add((R) dogReport);
        }
        dogImages.addAll(fileService.getAllDogImages());
        catImages.addAll(fileService.gatAllCatImages());
    }

    public List<Cat> getCatsByPetOwnerIdFromCache(Long petOwnerId) {
        return catsByPetOwnerId.get(petOwnerId);
    }

    public List<Dog> getDogByPetOwnerIdFromCache(Long petOwnerId) {
        return dogsByPetOwnerId.get(petOwnerId);
    }

    public void setAllAnimalsReportedToFalse() {
        for (Map.Entry<Long, List<Cat>> entry : catsByPetOwnerId.entrySet()) {
            for (Cat cat : entry.getValue()) {
                cat.setReported(false);
                catService.putPet(cat);
            }
        }
        for (Map.Entry<Long, List<Dog>> entry : dogsByPetOwnerId.entrySet()) {
            for (Dog dog : entry.getValue()) {
                dog.setReported(false);
                dogService.putPet(dog);
            }
        }
    }

    public Volunteer appointVolunteerToCheckReports(Long chatId) {
        Volunteer volunteer = volunteers.get(chatId);
        if (volunteer != null) {
            if (volunteer.isFree() && !volunteer.isCheckingReports() && volunteer.isInOffice()) {
                volunteer.setCheckingReports(true);
                volunteer.setFree(false);
                return volunteers.put(chatId, volunteerService.putVolunteer(volunteer));
            }
            if (!volunteer.isFree() && volunteer.isInOffice() && !volunteer.isCheckingReports()) {
                volunteer.setCheckingReports(true);
                return volunteers.put(chatId, volunteerService.putVolunteer(volunteer));
            }
        }
        return null;
    }

    public void volunteerAcceptReport(Long volunteerId, R report) {
        cashedReports.remove(report);
        report.setCheckedByVolunteer(true);
        cashedReports.add(report);
        if (report instanceof DogReport dogReport) {
            dogReportService.putReport(dogReport);
        } else if (report instanceof CatReport catReport) {
            catReportService.putReport(catReport);
        }
        Volunteer volunteer = volunteers.get(volunteerId);
        volunteer.setCheckingReports(false);
        volunteers.put(volunteerId, volunteer);
    }

    public void volunteerRejectReport(Long volunteerId, R report) {
        cashedReports.remove(report);
        if (report instanceof DogReport dogReport) {
            dogReportService.deleteReport(dogReport);
            dogsByPetOwnerId.get(dogReport.getCopiedPetOwnerId())
                    .stream()
                    .filter(d -> d.getId()
                            .equals(dogReport.getCopiedAnimalId()))
                    .findFirst()
                    .ifPresent(dog -> {
                        dog.setReported(false);
                        dogService.putPet(dog);
                    });
        } else if (report instanceof CatReport catReport) {
            catReportService.deleteReport(catReport);
            catsByPetOwnerId.get(catReport.getCopiedPetOwnerId())
                    .stream()
                    .filter(c -> c.getId()
                            .equals(catReport.getCopiedAnimalId()))
                    .findFirst()
                    .ifPresent(cat -> {
                        cat.setReported(false);
                        catService.putPet(cat);
                    });
        }
        volunteers.entrySet().stream().peek(entry -> {
            if (entry.getKey().equals(volunteerId)) {
                entry.setValue(Stream.of(entry.getValue()).peek(vol ->
                        vol.setCheckingReports(false)).findFirst().get());
            }
        });
    }

    public void volunteerWantsToGetOutFromOffice(Long chatId) {
        volunteers.entrySet().stream().peek(entry -> {
            if (entry.getKey().equals(chatId)) {
                entry.setValue(Stream.of(entry.getValue()).peek(vol -> {
                    vol.setCheckingReports(false);
                    vol.setInOffice(false);
                    vol.setFree(true);
                }).findFirst().get());
            }
        });
    }

    public void createReportForAnimal(Long chatId, A animal) {
        PetOwner petOwner = petOwnersById.get(chatId);
        if (animal instanceof Cat) {
            CatReport catReport = CatReport.builder().cat((Cat) animal).petOwner(petOwner).images(new ArrayList<>()).build();
            catReport.setPetOwner(petOwner);
            CatReport catReportWithId = catReportService.putReport(catReport);
            actualReportByPetOwnerId.put(chatId, (R) catReportWithId);
            actualPetsInReportProcess.put(chatId, animal);
        } else if (animal instanceof Dog dog) {
            DogReport dogReport = DogReport.builder().dog(dog).petOwner(petOwner).images(new ArrayList<>()).build();
            dogReport.setPetOwner(petOwner);
            DogReport dogReportWithId = dogReportService.putReport(dogReport);
            actualReportByPetOwnerId.put(chatId, (R) dogReportWithId);
            actualPetsInReportProcess.put(chatId, animal);
        }
    }

    public Volunteer findFreeVolunteer() {
        for (Map.Entry<Long, Volunteer> entry : volunteers.entrySet()) {
            if (entry.getValue().isFree()) return entry.getValue();
        }
        throw new NotFoundInDataBaseException("Волонтеры заняты. Придется подождать. Обратитесь позже");
    }
}
