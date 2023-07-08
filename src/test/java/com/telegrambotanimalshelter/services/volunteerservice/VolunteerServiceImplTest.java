package com.telegrambotanimalshelter.services.volunteerservice;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.repositories.VolunteerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceImplTest {

    @InjectMocks
    VolunteerServiceImpl volunteerService;

    @Mock
    VolunteerRepository volunteerRepository;

    Long id = 123L;
    Volunteer volunteer = new Volunteer();
    List<Volunteer> volunteers = List.of(volunteer);

    @Test
    void findVolunteer() {
        when(volunteerRepository.findById(id)).thenReturn(Optional.ofNullable(volunteer));
        assertEquals(volunteerService.findVolunteer(id), volunteer);
        assertThrows(NotFoundInDataBaseException.class, ()-> volunteerService.findVolunteer(0L));
    }

    @Test
    void checkVolunteer() {
        when(volunteerRepository.findById(id)).thenReturn(Optional.ofNullable(volunteer));
        assertTrue(volunteerService.checkVolunteer(id));
        assertFalse(volunteerService.checkVolunteer(0L));
    }

    @Test
    void saveVolunteer() {
        when(volunteerRepository.save(volunteer)).thenReturn(volunteer);
        assertEquals(volunteerService.saveVolunteer(volunteer), volunteer);
    }

    @Test
    void deleteVolunteer() {
        assertEquals(volunteerService.deleteVolunteer(volunteer), HttpStatus.OK);
    }

    @Test
    void putVolunteer() {
        when(volunteerRepository.save(volunteer)).thenReturn(volunteer);
        assertEquals(volunteerService.putVolunteer(volunteer), volunteer);
    }

    @Test
    void setFree() {
        volunteer.setFree(true);
        volunteer.setId(id);
        volunteer.setPetOwner(null);

        when(volunteerRepository.findById(id)).thenReturn(Optional.ofNullable(volunteer));
        when(volunteerRepository.save(volunteer)).thenReturn(volunteer);
        assertEquals(volunteerService.setFree(id, true), volunteer);

    }

    @Test
    void findFreeVolunteer() {
        List<Volunteer> notFreeVolunteers = List.of();

        when(volunteerRepository.findVolunteersByIsFreeTrue()).thenReturn(this.volunteers);
        assertEquals(volunteerService.findFreeVolunteer(), volunteer);

        when(volunteerRepository.findVolunteersByIsFreeTrue()).thenReturn(notFreeVolunteers);
        assertThrows(NotFoundInDataBaseException.class, ()-> volunteerService.findFreeVolunteer());
    }

    @Test
    void gatAllVolunteers() {
        when(volunteerRepository.findAll()).thenReturn(volunteers);
        assertEquals(volunteerService.gatAllVolunteers(), volunteers);
    }
}