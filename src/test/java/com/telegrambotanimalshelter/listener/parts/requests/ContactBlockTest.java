package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.telegrambotanimalshelter.listener.parts.keeper.Cache;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.telegrambotanimalshelter.utils.ConstantsForTesting.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContactBlockTest<A extends Animal, R extends Report> {


    @Mock
    private MessageSender<A> sender;

    @Mock
    private CacheKeeper<A, R> keeper;

    @Mock
    private PetOwnersService petOwnersService;

    @InjectMocks
    private ContactBlock<A, R> out;

    private Cache<A, R> cache;

    final String json = Files.readString(Path.of(
            ContactBlockTest.class
                    .getResource("update.json").toURI()));

    final String json2 = Files.readString(Path.of(
            ContactBlockTest.class
                    .getResource("update2.json").toURI()));

    final String messageJson = Files.readString(Path.of(
            ContactBlockTest.class
                    .getResource("message.json").toURI()));

    ContactBlockTest() throws IOException, URISyntaxException {
    }

    @BeforeEach
    void setUp() {
        cache = new Cache<>();
        when(keeper.getCache()).thenReturn(cache);
    }

    @Test
    void contactsRequestBlock_ReturnsPetOwnerWhoChangedFirstName() {
        cache.setPetOwnersById(petOwners);
        PetOwner petOwner = petOwner1;
        String info = "Имя: Имя усыновителя";
        petOwner.setFirstName(info);
        Message message = BotUtils.fromJson(messageJson.replace(
                "%text%", info), Message.class);
        PetOwner testingPetOwner =
                out.contactsRequestBlock(petOwner1.getId(), message);
        assertEquals(petOwner, testingPetOwner);
    }

    @Test
    void contactsRequestBlock_ReturnsPetOwnerWhoChangedLastName() {
        cache.setPetOwnersById(petOwners);
        PetOwner petOwner = petOwner1;
        String info = "Фамилия: Фамилия усыновителя";
        String preFix = info.split(" ")[0];
        String lastName = info.substring(preFix.length());
        petOwner.setLastName(lastName);
        Message message = BotUtils.fromJson(messageJson.replace(
                "%text%", info), Message.class);
        PetOwner testingPetOwner =
                out.contactsRequestBlock(petOwner1.getId(), message);
        assertEquals(petOwner, testingPetOwner);
    }

    @Test
    void contactsRequestBlock_ReturnsPetOwnerWhoChangedPhoneNumber() {

    }

    @Test
    void contactsRequestBlock_ReturnsPetOwnerWhoForcedStopContactRequest() {
        cache.setPetOwnersById(petOwners);
        String info = "/break";
        Message message = BotUtils.fromJson(messageJson.replace(
                "%text%", info), Message.class);
        PetOwner testingPetOwner =
                out.contactsRequestBlock(petOwner1.getId(), message);
        assertEquals(petOwner1, testingPetOwner);
    }

    @Test
    void contactsRequestBlock_ReturnsPetOwnerWhoSentNotRequiredTextInMessage() {
        cache.setPetOwnersById(petOwners);
        String info = "Вообще любое сообщение, не определяющее действия";
        Message message = BotUtils.fromJson(messageJson.replace(
                "%text%", info), Message.class);
        PetOwner testingPetOwner =
                out.contactsRequestBlock(petOwner1.getId(), message);
        assertEquals(petOwner1, testingPetOwner);
    }

    @Test
    void savePotentialPetOwner_ReturnsPetOwnerWhoWasSaved() {
        Update update = BotUtils.fromJson(json.replace(
                "%text%", "просто текст"), Update.class);
        cache.setVolunteers(volunteersForContactRequestBlock);
        when(petOwnersService.savePotentialPetOwner(update))
                .thenReturn(petOwner1);
        PetOwner petOwnerForTesting =
                out.savePotentialPetOwner(update);
        assertEquals(petOwnerForTesting, petOwner1);
    }

    @Test
    void savePotentialPetOwner_ReturnsNullIfPetOwnerExist() {
        Update update = BotUtils.fromJson(json2.replace(
                "%text%", "просто текст"), Update.class);
        cache.setVolunteers(volunteersForContactRequestBlock);
        when(petOwnersService.savePotentialPetOwner(update))
                .thenReturn(null);
        PetOwner petOwnerForTesting =
                out.savePotentialPetOwner(update);
        assertNull(petOwnerForTesting);
    }

    @Test
    void checkContactRequestStatus_ReturnsFalseIfPetOwnerIsNotInContactRequestBlock() {
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
        cache = new Cache<>();
        cache.setPetOwnersById(petOwners);
        when(keeper.getCache()).thenReturn(cache);
        boolean falseIfPetOwnerIsNotInContactRequestBlock =
                out.checkContactRequestStatus(petOwner1.getId());
        assertFalse(falseIfPetOwnerIsNotInContactRequestBlock);
    }

    @Test
    void checkContactRequestStatus_ReturnsFalseIfPetOwnerIsNotExist() {
        cache.setPetOwnersById(petOwners);
        boolean falseIfPetOwnerIsNotExist =
                out.checkContactRequestStatus(10L);
        assertFalse(falseIfPetOwnerIsNotExist);
    }

    @Test
    void checkContactRequestStatus_ReturnsTrueIfPetOwnerIsInContactRequestBlock() {
        Map<Long, PetOwner> petOwnersForTesting = new HashMap<>();
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
        petOwnersForTesting.put(petOwner1.getId(), petOwner1);
        petOwnersForTesting.put(petOwner2.getId(), petOwner2);

        petOwnersForTesting.forEach((key, value)
                -> value.setContactRequest(true));
        cache.setPetOwnersById(petOwnersForTesting);
        boolean trueIfPetOwnerIsInContactRequestBlock =
                out.checkContactRequestStatus(petOwner1.getId());
        assertTrue(trueIfPetOwnerIsInContactRequestBlock);
    }

    @Test
    void sendMessageToTakeName_ReturnsPetOwnerWhoGotInContactRequestBlock() {
        Map<Long, PetOwner> petOwnersForTesting = new HashMap<>();
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
        petOwnersForTesting.put(petOwner1.getId(), petOwner1);
        petOwnersForTesting.put(petOwner2.getId(), petOwner2);
        petOwner1.setContactRequest(true);
        cache.setPetOwnersById(petOwnersForTesting);
        when(petOwnersService.setPetOwnerContactRequest(petOwner2.getId(), true))
                .thenReturn(petOwner1);
        PetOwner petOwnerForTesting =
                out.sendMessageToTakeName(petOwner1.getId());
        assertEquals(petOwnerForTesting, petOwner1);
    }


}