package com.telegrambotanimalshelter.listener.parts.keeper;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petservice.PetService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class Keeper {

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private final PetService<Cat> catPetService;

    private final PetService<Dog> dogPetService;

    private HashMap<Long, PetOwner> cashedPetOwners;

    private HashMap<Long, List<Cat>> cashedCats;

    private HashMap<Long, List<Dog>> cashedDogs;

    private HashMap<Long, Volunteer> cashedVolunteers;

    private HashMap<Long, ? extends Report> cashedReports;

    public Keeper(PetOwnersService petOwnersService, VolunteerService volunteerService,
                  PetService<Cat> catPetService, PetService<Dog> dogPetService) {
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.catPetService = catPetService;
        this.dogPetService = dogPetService;
        this.cashedVolunteers = new HashMap<>();
        this.cashedPetOwners = new HashMap<>();
        this.cashedReports = new HashMap<>();
        this.cashedCats = new HashMap<>();
        this.cashedDogs = new HashMap<>();
    }

    @PostConstruct
    public void fill() {
        for (PetOwner petOwner : petOwnersService.getAllPetOwners()) {
            cashedPetOwners.put(petOwner.getId(), petOwner);
            cashedCats.put(petOwner.getId(), catPetService.findPetsByPetOwner(petOwner));
            cashedDogs.put(petOwner.getId(), dogPetService.findPetsByPetOwner(petOwner));
        }
        for (Volunteer volunteer : volunteerService.gatAllVolunteers()) {
            cashedVolunteers.put(volunteer.getId(), volunteer);
        }

    }

    public Volunteer findFreeVolunteer() {
        for (Map.Entry<Long, Volunteer> entry : cashedVolunteers.entrySet()) {
            if (entry.getValue().isFree()) return entry.getValue();
        }
        throw new NotFoundInDataBaseException("Волонтеры заняты. ПРидется подождать");
    }
}
