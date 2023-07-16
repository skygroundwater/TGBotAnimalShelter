package com.telegrambotanimalshelter.services.volunteerservice;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.repositories.VolunteersRepository;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class VolunteersServiceImpl implements VolunteersService {

    private final VolunteersRepository volunteersRepository;

    public VolunteersServiceImpl(VolunteersRepository volunteersRepository) {
        this.volunteersRepository = volunteersRepository;
    }

    @Override
    public Volunteer findByName(String name){
        return volunteersRepository.findByUserName(name);
    }

    @Override
    public Volunteer findVolunteer(Long id) {
        return volunteersRepository.findById(id).orElseThrow(
                () -> new NotFoundInDataBaseException("Волонтер не был найден"));
    }

    @Override
    public boolean checkVolunteer(Long id) {
        try {
            return findVolunteer(id) != null;
        } catch (NotFoundInDataBaseException e) {
            return false;
        }
    }

    @Override
    public Volunteer saveVolunteer(Volunteer volunteer) {
        return volunteersRepository.save(volunteer);
    }

    @Override
    public HttpStatus deleteVolunteer(Volunteer volunteer) {
        volunteersRepository.delete(volunteer);
        return HttpStatus.OK;
    }

    @Override
    public Volunteer putVolunteer(Volunteer volunteer) {
        return volunteersRepository.save(volunteer);
    }

    @Override
    public Volunteer setFree(Long id, boolean trueOrFalse) {
        Volunteer volunteer = findVolunteer(id);
        volunteer.setFree(trueOrFalse);
        if (trueOrFalse) {
            volunteer.setPetOwner(null);
        }
        return volunteersRepository.save(volunteer);
    }

    @Override
    public Volunteer findFreeVolunteer() {
        return volunteersRepository.findVolunteersByIsFreeTrue().stream().findAny()
                .orElseThrow(() -> new NotFoundInDataBaseException("Все волонтеры на данный момент заняты. Просим вас подождать"));
    }

    @Override
    public List<Volunteer> getAllVolunteers() {
        return volunteersRepository.findAll();
    }
}