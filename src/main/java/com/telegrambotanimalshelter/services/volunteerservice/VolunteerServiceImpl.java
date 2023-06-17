package com.telegrambotanimalshelter.services.volunteerservice;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.repositories.VolunteerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public VolunteerServiceImpl(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public Volunteer findVolunteer(Long id) {
        return volunteerRepository.findById(id).orElseThrow(() -> new NotFoundInDataBaseException("Волонтер не был найден"));
    }


    @Override
    public boolean checkVolunteer(Long id) {
        try {
            return findVolunteer(id) != null;
        }catch (NotFoundInDataBaseException e){
            return false;
        }
    }

    @Override
    public Volunteer saveVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    @Override
    public HttpStatus deleteVolunteer(Volunteer volunteer) {
        volunteerRepository.delete(volunteer);
        return HttpStatus.OK;
    }

    @Override
    public Volunteer putVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    @Override
    public Volunteer findFreeVolunteer() {
        return volunteerRepository.findVolunteersByIsFreeTrue().stream().findAny()
                .orElseThrow(() -> new NotFoundInDataBaseException("Все волонтеры на данный момент заняты. Просим вас подождать"));
    }
}