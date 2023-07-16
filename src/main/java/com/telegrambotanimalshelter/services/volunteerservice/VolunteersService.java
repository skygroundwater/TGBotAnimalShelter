package com.telegrambotanimalshelter.services.volunteerservice;

import com.telegrambotanimalshelter.models.Volunteer;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface VolunteersService {
    Volunteer findByUserName(String name);

    Volunteer findVolunteer(Long id);

    boolean checkVolunteer(Long id);

    Volunteer saveVolunteer(Volunteer volunteer);

    HttpStatus deleteVolunteer(Volunteer volunteer);

    Volunteer putVolunteer(Volunteer volunteer);

    Volunteer setFree(Long id, boolean trueOrFalse);

    Volunteer findFreeVolunteer();

    List<Volunteer> getAllVolunteers();
}