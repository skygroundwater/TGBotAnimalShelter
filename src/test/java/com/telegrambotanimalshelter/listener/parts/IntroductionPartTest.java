package com.telegrambotanimalshelter.listener.parts;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IntroductionPartTest<A extends Animal> {

    @Mock
    MessageSender<Animal> sender;

    @InjectMocks
    IntroductionPart out;

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
        //given
        out = new IntroductionPart(sender);

        dogShelter = new Shelter(dogShelterName);
        dogShelter.setShelterType(ShelterType.DOGS_SHELTER);

        catShelter = new Shelter(catShelterName);
        catShelter.setShelterType(ShelterType.CATS_SHELTER);

        when(sender.sendResponse(any(SendMessage.class)))
                .thenReturn(okSendResponse);
    }

    @Test
    public void welcome_ThrowsRunTimeException(){
        Assertions.assertThrows(RuntimeException.class,
                () -> out.welcome(104235343L, null));
    }

    @Test
    void welcome_ReturnsOkResponse() {
        //when
        dogShelterSendResponse = out.welcome(anyLong(), dogShelter);
        catShelterSendResponse = out.welcome(anyLong(), catShelter);
        assertions();
    }

    @Test
    void shelterInfo_ReturnsOkResponse() {
        //when
        dogShelterSendResponse = out.shelterInfo(anyLong(), dogShelter);
        catShelterSendResponse = out.shelterInfo(anyLong(), catShelter);
        assertions();
    }

    @Test
    void shelterWorkingHours_ReturnsOkResponse() {
        //when
        dogShelterSendResponse = out.shelterWorkingHours(anyLong(), dogShelter);
        catShelterSendResponse = out.shelterWorkingHours(anyLong(), catShelter);
        assertions();
    }

    @Test
    void shelterPass_ReturnsOkResponse() {
        //when
        dogShelterSendResponse = out.shelterPass(anyLong(), dogShelter);
        catShelterSendResponse = out.shelterPass(anyLong(), catShelter);
        assertions();
    }

    @Test
    void shelterSafety_ReturnsOkResponse() {
        //when
        dogShelterSendResponse = out.shelterSafety(anyLong(), dogShelter);
        catShelterSendResponse = out.shelterSafety(anyLong(), catShelter);
        assertions();
    }

    private void assertions() {
        //then
        assertEquals(SendResponse.class, dogShelterSendResponse.getClass());
        assertEquals(SendResponse.class, catShelterSendResponse.getClass());
        assertTrue(catShelterSendResponse.isOk());
        assertTrue(dogShelterSendResponse.isOk());
    }
}