package com.telegrambotanimalshelter.services.petownerservice;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.repositories.PetOwnersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetOwnersServiceImplTest {

    @Mock
    PetOwnersRepository petOwnersRepository;
    @Mock
    Update update;
    @Mock
    Message message;
    @Mock
    Chat chat;

    @InjectMocks
    PetOwnersServiceImpl petOwnersService;
    PetOwner petOwner = new PetOwner();
    Volunteer volunteer = new Volunteer();

    @Test
    void postPetOwnerIfNull() {
        assertThrows(NotValidDataException.class, () -> petOwnersService.postPetOwner(null));
    }

    @Test
    void postPetOwner() {
        when(petOwnersRepository.save(petOwner)).thenReturn(petOwner);

        assertEquals(petOwnersService.postPetOwner(petOwner), petOwner);
    }

    @Test
    void deletePetOwner() {
        assertEquals(petOwnersService.deletePetOwner(petOwner), HttpStatus.OK);
    }

    @Test
    void findPetOwnerIfNotExist() {
        assertThrows(NotFoundInDataBaseException.class, () -> petOwnersService.findPetOwner(0L));
    }

    @Test
    void findPetOwner() {
        when(petOwnersRepository.findById(1L))
                .thenReturn(Optional.ofNullable(petOwner));

        assertEquals(petOwnersService.findPetOwner(1L), petOwner);
    }


    @Test
    void putPetOwner() {
        when(petOwnersRepository.save(petOwner)).thenReturn(petOwner);

        assertEquals(petOwnersService.putPetOwner(petOwner), petOwner);
    }

    @Test
    void savePotentialPetOwner() {
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(petOwnersRepository.findById(chat.id())).thenReturn(Optional.ofNullable(petOwner));

        assertEquals(petOwnersService.savePotentialPetOwner(update), petOwner);
    }

    @Test
    void savePotentialPetOwnerIfNotExistInDB() {
        PetOwner petOwner1 = new PetOwner(123L, "firstName", "lastName",
                "username", LocalDateTime.now(), false);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);

        when(petOwnersRepository.findById(0L)).thenReturn(Optional.of(new PetOwner(123L, "firstName", "lastName",
                "username", LocalDateTime.now(), false)));

        assertEquals(petOwnersService.savePotentialPetOwner(update).getFirstName(), petOwner1.getFirstName());
    }

    @Test
    void setPetOwnerReportRequest() {
        petOwner.setReportRequest(true);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenReturn(Optional.ofNullable(petOwner));
        when(petOwnersRepository.save(petOwner)).thenReturn(petOwner);

        assertEquals(petOwnersService.setPetOwnerReportRequest(123L, true), petOwner);
    }

    @Test
    void checkReportRequestStatusIfTrue() {
        petOwner.setReportRequest(true);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenReturn(Optional.ofNullable(petOwner));

        assertTrue(petOwnersService.checkReportRequestStatus(123L));
    }

    @Test
    void checkReportRequestStatusIfFalse() {
        petOwner.setReportRequest(false);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenThrow(NotFoundInDataBaseException.class);

        assertFalse(petOwnersService.checkReportRequestStatus(123L));
    }

    @Test
    void checkContactRequestStatusIfTrue() {
        petOwner.setContactRequest(true);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenReturn(Optional.ofNullable(petOwner));

        assertTrue(petOwnersService.checkContactRequestStatus(123L));
    }

    @Test
    void checkContactRequestStatusIfFalse() {
        petOwner.setContactRequest(false);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenThrow(NotFoundInDataBaseException.class);

        assertFalse(petOwnersService.checkContactRequestStatus(123L));
    }

    @Test
    void setPetOwnerContactRequest() {
        petOwner.setContactRequest(true);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenReturn(Optional.ofNullable(petOwner));
        when(petOwnersRepository.save(petOwner)).thenReturn(petOwner);

        assertEquals(petOwnersService.setPetOwnerContactRequest(123L, true), petOwner);
    }


    @Test
    void setPetOwnerToVolunteerChat() {
        petOwner.setVolunteerChat(true);
        petOwner.setId(123L);
        petOwner.setVolunteer(volunteer);

        when(petOwnersRepository.findById(123L)).thenReturn(Optional.ofNullable(petOwner));
        when(petOwnersRepository.save(petOwner)).thenReturn(petOwner);

        assertEquals(petOwnersService.setPetOwnerToVolunteerChat(123L, volunteer, true), petOwner);
    }

    @Test
    void checkVolunteerChatStatusIfPetOwnerExist() {
        petOwner.setVolunteerChat(true);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenReturn(Optional.ofNullable(petOwner));
        assertTrue(petOwnersService.checkVolunteerChatStatus(123L));
    }

    @Test
    void checkVolunteerChatStatusIfPetOwnerNotExist() {
        petOwner.setVolunteerChat(false);
        petOwner.setId(123L);

        when(petOwnersRepository.findById(123L)).thenThrow(NotFoundInDataBaseException.class);

        assertFalse(petOwnersService.checkVolunteerChatStatus(123L));
    }

    @Test
    void findActualPetOwners() {
        List<PetOwner> petOwners = List.of(petOwner);

        when(petOwnersRepository.findPetOwnersByHasPetsTrue()).thenReturn(petOwners);

        assertEquals(petOwnersService.findActualPetOwners(), petOwners);
    }

    @Test
    void getAllPetOwners() {
        List<PetOwner> petOwners = List.of(petOwner);

        when(petOwnersRepository.findAll()).thenReturn(petOwners);

        assertEquals(petOwnersService.getAllPetOwners(), petOwners);
    }
}