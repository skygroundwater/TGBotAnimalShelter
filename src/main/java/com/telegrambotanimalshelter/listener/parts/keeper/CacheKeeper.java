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
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.*;

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

    private HashMap<Long, PetOwner> petOwners;

    private HashMap<Long, Volunteer> volunteers;

    private HashMap<Long, List<Cat>> cats;

    private HashMap<Long, List<Dog>> dogs;

    private HashMap<Long, List<CatReport>> catReports;

    private HashMap<Long, List<DogReport>> dogReports;

    private ArrayList<R> cashedReports;

    private HashMap<Long, R> actualReportByPetOwnerId;

    private HashMap<Long, A> actualPetsInReportProcess;

    private ArrayList<CatImage> catImages;

    private ArrayList<DogImage> dogImages;

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
        this.petOwners = new HashMap<>();
        this.cats = new HashMap<>();
        this.dogs = new HashMap<>();
        this.catReports = new HashMap<>();
        this.dogReports = new HashMap<>();
        this.actualReportByPetOwnerId = new HashMap<>();
        this.actualPetsInReportProcess = new HashMap<>();
        this.cashedReports = new ArrayList<>();
        this.catImages = new ArrayList<>();
        this.dogImages = new ArrayList<>();
        fill();
    }

    public void fill() {
        for (PetOwner petOwner : petOwnersService.getAllPetOwners()) {
            petOwners.put(petOwner.getId(), petOwner);
            cats.put(petOwner.getId(), catService.findPetsByPetOwner(petOwner));
            dogs.put(petOwner.getId(), dogService.findPetsByPetOwner(petOwner));
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
        return cats.get(petOwnerId);
    }

    public List<Dog> getDogByPetOwnerIdFromCache(Long petOwnerId) {
        return dogs.get(petOwnerId);
    }

    public PetOwner findCashedPetOwnerById(Long chatId) {
        return petOwners.get(chatId);
    }

    public void setAllAnimalsReportedToFalse() {
        for (Map.Entry<Long, List<Cat>> entry : cats.entrySet()) {
            for (Cat cat : entry.getValue()) {
                cat.setReported(false);
                catService.putPet(cat);
            }
        }
        for (Map.Entry<Long, List<Dog>> entry : dogs.entrySet()) {
            for (Dog dog : entry.getValue()) {
                dog.setReported(false);
                dogService.putPet(dog);
            }
        }
    }

    public Volunteer appointVolunteerToCheckReports(Long chatId) {
        Volunteer volunteer = volunteers.get(chatId);
        if (volunteer.isFree() && !volunteer.isCheckingReports() && volunteer.isInOffice()) {
            volunteer.setCheckingReports(true);
            volunteer.setFree(false);
            return volunteers.put(chatId, volunteerService.putVolunteer(volunteer));
        }
        if (!volunteer.isFree() && volunteer.isInOffice() && !volunteer.isCheckingReports()) {
            volunteer.setCheckingReports(true);
            return volunteers.put(chatId, volunteerService.putVolunteer(volunteer));
        }
        return null;
    }

    public void volunteerAcceptReport(Long volunteerId, R report) {
        cashedReports.remove(report);
        if (report instanceof DogReport dogReport) {
            dogReport.setCheckedByVolunteer(true);
            cashedReports.add((R) dogReportService.putReport(dogReport));
            System.out.println(cashedReports);
        } else if (report instanceof CatReport catReport) {
            catReport.setCheckedByVolunteer(true);
            catReportService.putReport(catReport);
            cashedReports.add((R) catReportService.putReport(catReport));
            System.out.println(cashedReports);
        }
        Volunteer volunteer = volunteers.get(volunteerId);
        volunteer.setCheckingReports(false);
        volunteers.put(volunteerId, volunteer);
    }

    public void volunteerRejectReport(Long volunteerId, R report) {
        cashedReports.remove(report);
        if (report instanceof DogReport dogReport) {
            dogReportService.deleteReport(dogReport);
            Optional<Dog> optionalDog = dogs.get(
                            dogReport.getCopiedPetOwnerId())
                    .stream().filter(d -> d.getId()
                            .equals(dogReport.getCopiedAnimalId())).findFirst();
            if (optionalDog.isPresent()) {
                Dog dog = optionalDog.get();
                dog.setReported(false);
                dogService.putPet(dog);
            }
        } else if (report instanceof CatReport catReport) {
            catReportService.deleteReport(catReport);
            Optional<Cat> optionalCat = cats.get(
                            catReport.getCopiedPetOwnerId())
                    .stream().filter(c -> c.getId()
                            .equals(catReport.getCopiedAnimalId())).findFirst();
            if (optionalCat.isPresent()) {
                Cat cat = optionalCat.get();
                cat.setReported(false);
                catService.putPet(cat);
            }
        }
        Volunteer volunteer = volunteers.get(volunteerId);
        volunteer.setCheckingReports(false);
        volunteers.put(volunteerId, volunteer);
    }

    public void volunteerWantsToGetOutFromOffice(Long chatId) {
        Volunteer volunteer = volunteers.get(chatId);
        volunteer.setInOffice(false);
        volunteer.setCheckingReports(false);
        volunteer.setFree(true);
        volunteers.put(chatId, volunteerService.putVolunteer(volunteer));
    }

    public void createReportForAnimal(Long chatId, A animal) {
        PetOwner petOwner = findCashedPetOwnerById(chatId);
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

    public void saveReport(Long chatId) {
        R report = actualReportByPetOwnerId.get(chatId);
        if (report instanceof CatReport) {


        } else if (report instanceof DogReport) {


        }

    }

    public Volunteer findFreeVolunteer() {
        for (Map.Entry<Long, Volunteer> entry : volunteers.entrySet()) {
            if (entry.getValue().isFree()) return entry.getValue();
        }
        throw new NotFoundInDataBaseException("Волонтеры заняты. Придется подождать. Обратитесь позже");
    }
}
