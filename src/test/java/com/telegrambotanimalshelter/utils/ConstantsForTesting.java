package com.telegrambotanimalshelter.utils;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;

import java.time.LocalDateTime;
import java.util.*;

public class ConstantsForTesting {

    public static final PetOwner petOwner1 = new PetOwner(
            2L, "Карапет",
            "Карапетов", "karapet",
            LocalDateTime.now(),false
    );

    public static final PetOwner petOwner2 = new PetOwner(
            1L, "Пузанок",
            "Пузанов", "puzanok",
            LocalDateTime.now(), false
    );

    public static final Map<Long, PetOwner> petOwners =
            new HashMap<>(Map.of(
                    petOwner1.getId(), petOwner1,
                    petOwner2.getId(), petOwner2));

    public static final Volunteer volunteer1 =
            new Volunteer(
                    1L, "@afrodita",
                    "Афродита", "Боговиева",
                    "afrodita", true,
                    false,false,null);

    public static final Volunteer volunteer2 =
            new Volunteer(
                    2L, "@sohoncev",
                    "Владимир", "Сохонцев",
                    "sohonets", true,
                    false,false,null);

    public static final Map<Long, Volunteer> volunteers =
            new HashMap<>(Map.of(
                    volunteer1.getId(), volunteer1,
                    volunteer2.getId(), volunteer2));

    public static final List<CatReport> catReports =
            new ArrayList<>();

    public static final List<DogReport> dogReports =
            new ArrayList<>();

    public static final List<Report> reports =
            new ArrayList<>();

    public static final List<CatImage> catImages =
            new ArrayList<>();

    public static final List<DogImage> dogImages =
            new ArrayList<>();

    public static final Dog dog1 = new Dog(
            "Sharik", true,
            LocalDateTime.now(), petOwner1,
            "О собаке", null);

    public static final Dog dog2 = new Dog(
            "Shavka", false,
            LocalDateTime.now(), petOwner2,
            "О шавке", null);

    public static final Map<Long, List<Dog>> dogs =
            new HashMap<>(Map.of(
                    dog2.getPetOwner().getId(), List.of(dog2),
                    dog1.getPetOwner().getId(), List.of(dog1)
            ));

    public static final Cat cat1 = new Cat(
            "Murzik", false,
            LocalDateTime.now(), petOwner1,
            "О кошке", null);

    public static final Cat cat2 = new Cat(
            "Shavka", true,
            LocalDateTime.now(), petOwner2,
            "О шавке", null);

    public static final Map<Long, List<Cat>> cats =
            new HashMap<>(Map.of(
                    cat1.getPetOwner().getId(), List.of(cat1),
                    cat2.getPetOwner().getId(), List.of(cat2)));

    public static DogReport dogReport = new DogReport();

    static {
        dog1.setId(444L);
        dog2.setId(555L);
        cat1.setId(333L);
        cat2.setId(222L);
        dogReport.setPetOwner(petOwner1);
        dogReport.setDog(dog1);
        dogReport.setCheckedByVolunteer(false);
        dogReport.setDiet("просто диета");
        dogReport.setCommonDescriptionOfStatus("просто статус");
        dogReport.setBehavioralChanges("просто изменения");
        dogReport.setCopiedAnimalId(dog1.getId());
        dogReport.setDate(LocalDateTime.now().toLocalDate());
        dogReport.setImages(dogImages);
        dogReport.setCopiedPetOwnerId(petOwner1.getId());
        reports.addAll(catReports);
        reports.addAll(dogReports);
    }

}
