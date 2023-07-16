package com.telegrambotanimalshelter.listener.parts.keeper;

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
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Data
public class Cache<A extends Animal, R extends Report> {

    private ConcurrentMap<Long, PetOwner> petOwnersById;

    private ConcurrentMap<Long, Volunteer> volunteers;

    private ConcurrentMap<Long, List<Cat>> catsByPetOwnerId;

    private ConcurrentMap<Long, List<Dog>> dogsByPetOwnerId;

    private ConcurrentMap<Long, List<CatReport>> catReportsByCatId;

    private ConcurrentMap<Long, List<DogReport>> dogReportsByDogId;

    private List<R> cashedReports;

    private List<CatReport> cachedCatReports;

    private List<DogReport> cachedDogReports;

    private ConcurrentMap<Long, R> actualReportByPetOwnerId;

    private ConcurrentMap<Long, A> actualPetsInReportProcess;

    private List<A> cachedAnimals;

    private List<CatImage> catImages;

    private List<DogImage> dogImages;

    @PostConstruct
    public void init() {
        this.volunteers = new ConcurrentHashMap<>();
        this.petOwnersById = new ConcurrentHashMap<>();
        this.catsByPetOwnerId = new ConcurrentHashMap<>();
        this.dogsByPetOwnerId = new ConcurrentHashMap<>();
        this.catReportsByCatId = new ConcurrentHashMap<>();
        this.dogReportsByDogId = new ConcurrentHashMap<>();
        this.actualReportByPetOwnerId = new ConcurrentHashMap<>();
        this.actualPetsInReportProcess = new ConcurrentHashMap<>();
        this.cashedReports = new ArrayList<>();
        this.catImages = new ArrayList<>();
        this.dogImages = new ArrayList<>();
        this.cachedAnimals = new ArrayList<>();
    }
}
