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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class Cache<A extends Animal, R extends Report> {

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
    }

    public void clear(){
        volunteers.clear();
        petOwnersById.clear();
    }
}
