package com.telegrambotanimalshelter.listener.parts.requests;

import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.telegrambotanimalshelter.utils.ConstantsForTesting.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VolunteerAndPetOwnerChatTest<A extends Animal, R extends Report> {

    static long chatId = 123L;
    @Mock
    private PetOwnersService petOwnersService;

    @Mock
    private VolunteerService volunteerService;

    @Mock
    private MessageSender<A> sender;

    @Mock
    private CacheKeeper<A, R> keeper;

    @InjectMocks
    private VolunteerAndPetOwnerChat<A, R> out;

    Cache<A, R> cache;

    @BeforeEach
    void setUp() {
        cache = new Cache<>();
        when(keeper.getCache()).thenReturn(cache);
    }


    @Test
    void checkPetOwnerChatStatus_ReturnsTrue() {
        PetOwner petOwner = petOwner1;
        petOwner.setVolunteerChat(true);
        Map<Long, PetOwner> petOwnersForTest = petOwners;
        petOwnersForTest.put(petOwner.getId(), petOwner);
        cache.setPetOwnersById(petOwnersForTest);
        boolean petOwnerIsChattingOrNot =
                out.checkPetOwnerChatStatus(petOwner.getId());
        assertTrue(petOwnerIsChattingOrNot);
    }

    @Test
    void checkPetOwnerChatStatus_ReturnsFalse() {
        cache.setPetOwnersById(petOwners);
        boolean petOwnerIsChattingOrNot =
                out.checkPetOwnerChatStatus(petOwner1.getId());
        assertFalse(petOwnerIsChattingOrNot);
    }

    @Test
    void checkPetOwnerChatStatus_ReturnsFalseBecausePetOwnerIsNull() {
        Map<Long, PetOwner> petOwnersForTesting = petOwners;
        petOwnersForTesting.put(chatId, null);
        cache.setPetOwnersById(petOwnersForTesting);
        boolean petOwnerIsChattingOrNot =
                out.checkPetOwnerChatStatus(chatId);
        cache.getPetOwnersById().remove(chatId);
        assertFalse(petOwnerIsChattingOrNot);
    }

    @Test
    public void checkVolunteer_ReturnsTrueIfVolunteerIsNotInOfficeIsNotCheckingReportsAndNotFree() {
        Map<Long, Volunteer> volunteersForTesting = volunteers;
        Volunteer volunteerForTesting = volunteer1;
        volunteerForTesting.setInOffice(false);
        volunteerForTesting.setFree(false);
        volunteerForTesting.setCheckingReports(false);
        volunteersForTesting.put(volunteerForTesting.getId(), volunteerForTesting);
        cache.setVolunteers(volunteersForTesting);
        boolean volunteerIsNotInOfficeIsNotCheckingReportsAndNotFree =
                out.checkVolunteer(volunteerForTesting.getId());
        assertTrue(volunteerIsNotInOfficeIsNotCheckingReportsAndNotFree);
    }

    @Test
    public void checkVolunteer_ReturnsFalseIfVolunteerIsInOfficeOrIsCheckingReportsAndNotFree() {
        Map<Long, Volunteer> volunteersForTesting = volunteers;
        Volunteer volunteerForTesting = volunteer1;
        volunteerForTesting.setInOffice(true);
        volunteerForTesting.setFree(false);
        volunteerForTesting.setCheckingReports(false);
        volunteersForTesting.put(volunteerForTesting.getId(), volunteerForTesting);
        cache.setVolunteers(volunteersForTesting);
        boolean volunteerIsInOfficeIsNotCheckingReportsAndNotFree =
                out.checkVolunteer(volunteerForTesting.getId());
        assertFalse(volunteerIsInOfficeIsNotCheckingReportsAndNotFree);
    }

    @Test
    public void checkVolunteer_ReturnsFalseBecauseVolunteerIsNull() {
        Map<Long, Volunteer> volunteersForTesting = volunteers;
        volunteersForTesting.put(chatId, null);
        cache.setVolunteers(volunteersForTesting);
        boolean volunteerIsInOfficeIsNotCheckingReportsAndNotFree =
                out.checkVolunteer(chatId);
        assertFalse(volunteerIsInOfficeIsNotCheckingReportsAndNotFree);
    }


    @Test
    public void startChat_ReturnsInfoByStringThatChatIsStarted() {
        Volunteer volunteerForTesting = volunteer1;
        PetOwner petOwnerForTesting = petOwner1;
        petOwnerForTesting.setVolunteerChat(true);
        petOwnerForTesting.setVolunteer(volunteerForTesting);
        cache.setVolunteers(volunteers);
        cache.setPetOwnersById(petOwners);
        when(keeper.findFreeVolunteer()).thenReturn(volunteerForTesting);
        when(petOwnersService
                .setPetOwnerToVolunteerChat(
                        petOwnerForTesting.getId(),
                        volunteerForTesting,
                        true))
                .thenReturn(petOwnerForTesting);
        when(volunteerService.putVolunteer(volunteerForTesting))
                .thenReturn(volunteerForTesting);
        String info = "С вами будет общаться волонтёр " + volunteerForTesting.getFirstName();
        String infoForTesting =
                out.startChat(petOwnerForTesting.getId(), "просто дефолтное сообщение из блока");
        assertEquals(info, infoForTesting);
    }

    @Test
    public void startChat_ReturnsInfoByStringThatChatIsNotStartedBecauseWasThrownANotFoundInDataBaseException() {
        Map<Long, Volunteer> volunteersForTesting =
                volunteers;
        volunteersForTesting.forEach((key, value) -> value.setFree(false));
        cache.setVolunteers(volunteersForTesting);
        cache.setPetOwnersById(petOwners);
        when(keeper.findFreeVolunteer())
                .thenThrow(new NotFoundInDataBaseException("Волонтеры заняты. Придется подождать. Обратитесь позже"));
        when(petOwnersService
                .setPetOwnerToVolunteerChat(
                        petOwner1.getId(),
                        volunteersForTesting.get(2L),
                        true))
                .thenReturn(petOwner1);
        when(volunteerService.putVolunteer(volunteer1))
                .thenReturn(volunteer1);
        String info = "Волонтеры заняты. Придется подождать. Обратитесь позже";
        String infoForTesting =
                out.startChat(petOwner1.getId(), "просто дефолтное сообщение из блока");
        assertEquals(info, infoForTesting);
    }

    @Test
    public void stopChat_ReturnsFalseWhenCommandUnverifiable() {
        boolean chatIsNotStopped =
                out.stopChat(petOwner1.getId(),
                        null,
                        "да пошёл ты на!!! я таких волонтёров вертел");
        assertFalse(chatIsNotStopped);
    }

    @Test
    public void stopChat_ReturnsTrueWhenPetOwnerOrVolunteerWantToBreakAChat() {
        Map<Long, Volunteer> volunteersForTesting =
                volunteers;
        Map<Long, PetOwner> petOwnersForTesting =
                petOwners;
        volunteersForTesting.forEach((key, value) -> {
            value.setFree(false);
            value.setPetOwner(petOwnersForTesting.get(key));
        });
        petOwnersForTesting.forEach((key, value) -> {
            value.setVolunteerChat(true);
            value.setVolunteer(volunteersForTesting.get(key));
        });
        cache.setVolunteers(volunteersForTesting);
        cache.setPetOwnersById(petOwnersForTesting);
        when(volunteerService.setFree(volunteer1.getId(), true))
                .thenReturn(volunteer1);
        boolean chatIsStoppedByVolunteer =
                out.stopChat(null,
                        volunteer1.getId(),
                        "Прекратить чат");
        boolean chatIsStoppedByPetOwner =
                out.stopChat(petOwner1.getId(),
                        null,
                        "Прекратить чат");
        assertTrue(chatIsStoppedByPetOwner);
        assertTrue(chatIsStoppedByVolunteer);
    }

    @Test
    public void continueChat_ReturnsTrueWhenCommandUnverifiable() {
        PetOwner petOwner1 = new PetOwner(
                2L, "Карапет",
                "Карапетов", "karapet",
                LocalDateTime.now(), false
        );
        PetOwner petOwner2 = new PetOwner(
                1L, "Пузанок",
                "Пузанов", "puzanok",
                LocalDateTime.now(), false
        );
        Map<Long, PetOwner> petOwners =
                new HashMap<>(Map.of(
                        petOwner1.getId(), petOwner1,
                        petOwner2.getId(), petOwner2));
        Volunteer volunteer1 =
                new Volunteer(
                        1L, "@afrodita",
                        "Афродита", "Боговиева",
                        "afrodita", true,
                        false, false, null);
        Volunteer volunteer2 =
                new Volunteer(
                        2L, "@sohoncev",
                        "Владимир", "Сохонцев",
                        "sohonets", true,
                        false, false, null);
        Map<Long, Volunteer> volunteers =
                new HashMap<>(Map.of(
                        volunteer1.getId(), volunteer1,
                        volunteer2.getId(), volunteer2));
        volunteers.forEach((key, value) -> {
            value.setFree(false);
            value.setPetOwner(petOwners.get(key));
        });
        petOwners.forEach((key, value) -> {
            value.setVolunteerChat(true);
            value.setVolunteer(volunteers.get(key));
        });
        cache.setVolunteers(volunteers);
        cache.setPetOwnersById(petOwners);
        when(volunteerService.setFree(volunteer1.getId(), true))
                .thenReturn(volunteer1);
        boolean petOwnerContinuedChat =
                out.continueChat(petOwner2.getId(), null, "Сообщение волонтёру");
        boolean volunteerContinuedChat =
                out.continueChat(null, volunteer2.getId(), "Сообщение усыновителю");
        assertTrue(petOwnerContinuedChat);
        assertTrue(volunteerContinuedChat);
    }

    @Test
    public void continueChat_ReturnsFalseWhenCommandIsCorrect() {
        PetOwner petOwner1 = new PetOwner(
                2L, "Карапет",
                "Карапетов", "karapet",
                LocalDateTime.now(), false
        );
        PetOwner petOwner2 = new PetOwner(
                1L, "Пузанок",
                "Пузанов", "puzanok",
                LocalDateTime.now(), false
        );
        Map<Long, PetOwner> petOwners =
                new HashMap<>(Map.of(
                        petOwner1.getId(), petOwner1,
                        petOwner2.getId(), petOwner2));
        Volunteer volunteer1 =
                new Volunteer(
                        1L, "@afrodita",
                        "Афродита", "Боговиева",
                        "afrodita", true,
                        false, false, null);
        Volunteer volunteer2 =
                new Volunteer(
                        2L, "@sohoncev",
                        "Владимир", "Сохонцев",
                        "sohonets", true,
                        false, false, null);
        Map<Long, Volunteer> volunteers =
                new HashMap<>(Map.of(
                        volunteer1.getId(), volunteer1,
                        volunteer2.getId(), volunteer2));
        volunteers.forEach((key, value) -> {
            value.setFree(false);
            value.setPetOwner(petOwners.get(key));
        });
        petOwners.forEach((key, value) -> {
            value.setVolunteerChat(true);
            value.setVolunteer(volunteers.get(key));
        });
        cache.setVolunteers(volunteers);
        cache.setPetOwnersById(petOwners);
        when(volunteerService.setFree(volunteer1.getId(), true))
                .thenReturn(volunteer1);
        boolean volunteerContinuedChat =
                out.continueChat(null, volunteer2.getId(), "Прекратить чат");
        assertFalse(volunteerContinuedChat);
    }

}