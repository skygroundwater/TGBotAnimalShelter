package com.telegrambotanimalshelter.listener.parts.keeper;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class Keeper<A extends Animal> {

    private final PetOwnersService petOwnersService;

    private final VolunteerService volunteerService;

    private HashMap<Long, PetOwner> cashedPetOwners;

    private HashMap<Long, List<A>> cashedAnimals;

    private HashMap<Long, Volunteer> cashedVolunteers;

    public Keeper(PetOwnersService petOwnersService, VolunteerService volunteerService) {
        this.petOwnersService = petOwnersService;
        this.volunteerService = volunteerService;
        this.cashedVolunteers = new HashMap<>();
        this.cashedPetOwners = new HashMap<>();
    }

    @PostConstruct
    public void fill() {
        for (PetOwner petOwner : petOwnersService.getAllPetOwners()) {
            cashedPetOwners.put(petOwner.getId(), petOwner);
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
