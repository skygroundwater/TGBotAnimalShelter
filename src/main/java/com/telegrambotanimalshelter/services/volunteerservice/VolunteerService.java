package com.telegrambotanimalshelter.services.volunteerservice;

import com.telegrambotanimalshelter.models.Volunteer;
import org.springframework.http.HttpStatus;

public interface VolunteerService {
    Volunteer findVolunteer(Long id);


    boolean checkVolunteer(Long id);

    Volunteer saveVolunteer(Volunteer volunteer);

    HttpStatus deleteVolunteer(Volunteer volunteer);

    Volunteer putVolunteer(Volunteer volunteer);

    Volunteer setFree(Long id, boolean trueOrFalse);

    Volunteer findFreeVolunteer();
}