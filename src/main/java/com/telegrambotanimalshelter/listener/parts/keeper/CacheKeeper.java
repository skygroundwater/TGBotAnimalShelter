package com.telegrambotanimalshelter.listener.parts.keeper;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
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
import com.telegrambotanimalshelter.services.volunteerservice.VolunteersService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class CacheKeeper<A extends Animal, R extends Report> {

    private final PetOwnersService petOwnersService;

    private final VolunteersService volunteersService;

    private final PetService<Cat> catService;

    private final PetService<Dog> dogService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private final FileService<? extends AppImage> fileService;

    private final Cache<A, R> cache;

    public CacheKeeper(PetOwnersService petOwnersService,
                       VolunteersService volunteersService,
                       PetService<Cat> catService,
                       PetService<Dog> dogService,
                       @Qualifier("dogReportServiceImpl") ReportService<DogReport, Dog, DogImage> dogReportService,
                       @Qualifier("catReportServiceImpl") ReportService<CatReport, Cat, CatImage> catReportService,
                       FileService<? extends AppImage> fileService, Cache<A, R> cache) {
        this.petOwnersService = petOwnersService;
        this.volunteersService = volunteersService;
        this.catService = catService;
        this.dogService = dogService;
        this.catReportService = catReportService;
        this.dogReportService = dogReportService;
        this.fileService = fileService;
        this.cache = cache;
    }

    @PostConstruct
    private void init() {
        fillAnimalsCache();
        fillVolunteersCache();
        fillPetOwnersCache();
        fillReportsCache();
        fillImagesCache();
    }

    public String fillAnimalsCache() {
        catService.getAllPets().forEach(
                cat -> cache.getCachedAnimals().add((A) cat)
        );
        dogService.getAllPets().forEach(
                dog -> cache.getCachedAnimals().add((A) dog)
        );
        return "Кеш заполнен животными";
    }

    public String fillImagesCache() {
        cache.getDogImages().addAll(fileService.getAllDogImages());
        cache.getCatImages().addAll(fileService.getAllCatImages());
        return "Кеш заполнен фотографиями животных из базы данных";
    }

    public String fillVolunteersCache() {
        volunteersService.getAllVolunteers().forEach(
                volunteer -> cache.getVolunteers().put(volunteer.getId(), volunteer));
        return "Кеш заполнен волонтерами";
    }

    public String fillPetOwnersCache() {
        petOwnersService.getAllPetOwners().forEach(petOwner -> {
            cache.getPetOwnersById().put(petOwner.getId(), petOwner);
            cache.getCatsByPetOwnerId().put(petOwner.getId(), catService.findPetsByPetOwner(petOwner));
            cache.getDogsByPetOwnerId().put(petOwner.getId(), dogService.findPetsByPetOwner(petOwner));
        });
        return "Кеш заполнен усыновителями и их животными";
    }

    public String fillReportsCache() {
        for (CatReport catReport : catReportService.getAllReports()) {
            cache.getCachedReports().add((R) catReport);
        }
        for (DogReport dogReport : dogReportService.getAllReports()) {
            cache.getCachedReports().add((R) dogReport);
        }
        return "Кеш заполнен отчетами";
    }

    public Volunteer findFreeVolunteer() {
        for (Map.Entry<Long, Volunteer> entry :
                cache.getVolunteers().entrySet()) {
            if (entry.getValue().isFree()) return entry.getValue();
        }
        throw new NotFoundInDataBaseException("Волонтеры заняты. Придется подождать. Обратитесь позже");
    }
}
