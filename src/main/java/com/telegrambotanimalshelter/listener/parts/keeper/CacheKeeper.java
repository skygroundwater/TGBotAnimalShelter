package com.telegrambotanimalshelter.listener.parts.keeper;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petservice.PetService;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class CacheKeeper<A extends Animal, R extends Report> {

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private final PetService<Cat> catService;

    private final PetService<Dog> dogService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private HashMap<Long, PetOwner> petOwners;

    private HashMap<Long, Volunteer> volunteers;

    private HashMap<Long, List<Cat>> cats;

    private HashMap<Long, List<Dog>> dogs;

    private HashMap<Long, List<CatReport>> catReports;

    private HashMap<Long, List<DogReport>> dogReports;

    private HashMap<Long, R> actualReportsByPetOwnerId;

    private HashMap<Long, A> actualPetsInReportProcess;

    public CacheKeeper(PetOwnersService petOwnersService,
                       VolunteerService volunteerService,
                       PetService<Cat> catService,
                       PetService<Dog> dogService,
                       @Qualifier("dogReportServiceImpl") ReportService<DogReport, Dog, DogImage> dogReportService,
                       @Qualifier("catReportServiceImpl") ReportService<CatReport, Cat, CatImage> catReportService) {
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.catService = catService;
        this.dogService = dogService;
        this.catReportService = catReportService;
        this.dogReportService = dogReportService;
    }

    @PostConstruct
    public void init() {
        this.volunteers = new HashMap<>();
        this.petOwners = new HashMap<>();
        this.cats = new HashMap<>();
        this.dogs = new HashMap<>();
        this.catReports = new HashMap<>();
        this.dogReports = new HashMap<>();
        this.actualReportsByPetOwnerId = new HashMap<>();
        this.actualPetsInReportProcess = new HashMap<>();
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

    public void createReportForAnimal(Long chatId, A animal) {
        PetOwner petOwner = findCashedPetOwnerById(chatId);
        if (animal instanceof Cat) {
            CatReport catReport = CatReport.builder().cat((Cat) animal).petOwner(petOwner).images(new ArrayList<>()).build();
            catReport.setPetOwner(petOwner);
            actualReportsByPetOwnerId.put(chatId, (R) catReport);
            actualPetsInReportProcess.put(chatId, animal);
        } else if (animal instanceof Dog) {
            DogReport dogReport = DogReport.builder().dog((Dog) animal).petOwner(petOwner).images(new ArrayList<>()).build();
            dogReport.setPetOwner(petOwner);
            actualReportsByPetOwnerId.put(chatId, (R) dogReport);
            actualPetsInReportProcess.put(chatId, animal);
        }
    }

    public void saveReport(Long chatId) {
        R report = actualReportsByPetOwnerId.get(chatId);
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
