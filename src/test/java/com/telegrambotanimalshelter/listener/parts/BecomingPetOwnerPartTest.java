package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.telegrambotanimalshelter.utils.Constants.catShelterName;
import static com.telegrambotanimalshelter.utils.Constants.dogShelterName;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BecomingPetOwnerPartTest {

    @Mock
    MessageSender<Animal> sender;

    @InjectMocks
    BecomingPetOwnerPart out;

    Shelter dogShelter = null;

    Shelter catShelter = null;

    final SendResponse okSendResponse =
            BotUtils.fromJson(
                    """
                            {
                            "ok": true
                            }
                            """, SendResponse.class);

    SendResponse dogShelterSendResponse = null;
    SendResponse catShelterSendResponse = null;

    @BeforeEach
    void setUp() {
        out = new BecomingPetOwnerPart(sender);

        dogShelter = new Shelter(dogShelterName);
        dogShelter.setShelterType(ShelterType.DOGS_SHELTER);

        catShelter = new Shelter(catShelterName);
        catShelter.setShelterType(ShelterType.CATS_SHELTER);

        when(sender.sendResponse(any(SendMessage.class)))
                .thenReturn(okSendResponse);
    }

    @Test
    public void welcome_ReturnsOkResponse() {
        dogShelterSendResponse = out.welcome(anyLong(), dogShelter);
        catShelterSendResponse = out.welcome(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void welcome_ThrowsRunTimeException() {
        assertThrows(RuntimeException.class, () -> out.welcome(
                104532424L, new Shelter("Просто какой-то приют")));
    }

    @Test
    public void acquaintanceWithPet_ReturnsTrueResponse() {
        dogShelterSendResponse = out.acquaintanceWithPet(anyLong(), dogShelter);
        catShelterSendResponse = out.acquaintanceWithPet(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void documentsForPetOwner_ReturnsOkResponse() {
        dogShelterSendResponse = out.documentsForPetOwner(anyLong(), dogShelter);
        catShelterSendResponse = out.documentsForPetOwner(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void transportation_ReturnsOkResponse() {
        dogShelterSendResponse = out.transportation(anyLong(), dogShelter);
        catShelterSendResponse = out.transportation(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void homeForLittlePet_ReturnsOkResponse() {
        dogShelterSendResponse = out.homeForLittlePet(anyLong(), dogShelter);
        catShelterSendResponse = out.homeForLittlePet(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void homeForAdultPet_ReturnsOkResponse() {
        dogShelterSendResponse = out.homeForAdultPet(anyLong(), dogShelter);
        catShelterSendResponse = out.homeForAdultPet(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void homeForRestrictedPet_ReturnsOkResponse() {
        dogShelterSendResponse = out.homeForRestrictedPet(anyLong(), dogShelter);
        catShelterSendResponse = out.homeForRestrictedPet(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void reasonsForRefusal_ReturnsOkResponse() {
        dogShelterSendResponse = out.reasonsForRefusal(anyLong(), dogShelter);
        catShelterSendResponse = out.reasonsForRefusal(anyLong(), catShelter);
        //then
        assertions();
    }

    @Test
    public void firstMeetingWithDog_ReturnsOkResponse() {
        dogShelterSendResponse = out.firstMeetingWithDog(anyLong(), dogShelter);
        //then
        assertTrue(dogShelterSendResponse.isOk());
        assertEquals(SendResponse.class, dogShelterSendResponse.getClass());
    }

    private void assertions() {
        assertEquals(SendResponse.class, dogShelterSendResponse.getClass());
        assertEquals(SendResponse.class, catShelterSendResponse.getClass());
        assertTrue(catShelterSendResponse.isOk());
        assertTrue(dogShelterSendResponse.isOk());
    }
}
