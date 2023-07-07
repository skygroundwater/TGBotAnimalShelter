package com.telegrambotanimalshelter.listener.parts.checker;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.exceptions.NotReturnedResponseException;
import com.telegrambotanimalshelter.listener.parts.BecomingPetOwnerPart;
import com.telegrambotanimalshelter.listener.parts.IntroductionPart;
import com.telegrambotanimalshelter.listener.parts.requests.ChoosePetForPotentialOwnerBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ContactRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.listener.parts.volunteerblock.VolunteerBlock;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.reports.Report;
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.telegrambotanimalshelter.utils.Constants.catShelterName;
import static com.telegrambotanimalshelter.utils.Constants.dogShelterName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CallbackCheckerTest<A extends Animal, R extends Report, I extends AppImage> {

    String name = "Name";

    Long chatId = 123L;

    @Mock
    private ContactRequestBlock<A, R> contactBlock;

    @Mock
    private Shelter shelter;

    @Mock
    private ReportRequestBlock<A, R, I> reportRequestBlock;

    @Mock
    private VolunteerAndPetOwnerChat<A, R> chat;

    @Mock
    private IntroductionPart introductionPart;

    @Mock
    private BecomingPetOwnerPart becomingPart;

    @Mock
    Shelter dogShelter;

    @Mock
    Shelter catShelter;

    @Mock
    private MessageSender<A> sender;

    @Mock
    private VolunteerBlock<A, R, I> volunteerBlock;

    @Mock
    private ChoosePetForPotentialOwnerBlock<A, R> choosePetForPotentialOwnerBlock;

    @InjectMocks
    private CallbackChecker<A, R, I> out;

    final SendResponse okSendResponse =
            BotUtils.fromJson(
                    """
                            {
                            "ok": true
                            }
                            """, SendResponse.class);
    static CallbackQuery callbackQuery = null;

    final List<String> dataForCallBackQueries =
            new ArrayList<>(List.of("_shelter_info",
                    "_info", "_hours", "_pass", "_safety",
                    "_shelter_consultation", "_acquaintance",
                    "_documents", "_transportation",
                    "_little", "_adult", "_restricted",
                    "_reasons_for_refusal",
                    "back"
                    /*"_contacts",
                    "_report",
                    "i_am_volunteer",
                    "volunteer",*/

            ));

    final List<String> dataForConstantCallBackQueries =
            new ArrayList<>(List.of("_shelter_info",
                    "_info", "_hours", "_pass", "_safety",
                    "_shelter_consultation", "_acquaintance",
                    "_documents", "_transportation",
                    "_little", "_adult", "_restricted",
                    "_reasons_for_refusal"));

    final String json = Files.readString(Path.of(
            "/Users/olegmetelev/IdeaProjects/TGBotAnimalShelter/src/test/resources/com.telegrambotanimalshelter.listener/callbackquery.json"));

    CallbackCheckerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        //given
        dogShelter = new Shelter(dogShelterName);
        dogShelter.setShelterType(ShelterType.DOGS_SHELTER);

        catShelter = new Shelter(catShelterName);
        catShelter.setShelterType(ShelterType.CATS_SHELTER);

        out = new CallbackChecker<>(contactBlock, reportRequestBlock,
                chat, introductionPart, becomingPart, sender,
                dogShelter, catShelter, volunteerBlock, choosePetForPotentialOwnerBlock);

        when(introductionPart.welcome(123L, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterPass(123L, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterInfo(123L, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterWorkingHours(123L, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterSafety(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.welcome(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.documentsForPetOwner(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.acquaintanceWithPet(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.transportation(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.homeForLittlePet(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.homeForAdultPet(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.homeForRestrictedPet(123L, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.reasonsForRefusal(123L, dogShelter)).thenReturn(okSendResponse);

        when(sender.sendResponse(any(SendMessage.class))).thenReturn(okSendResponse);

        when(sender.sendStartMessage(123L)).thenReturn(okSendResponse);

    }

    @Test
    void callBackQueryCheck() throws IOException {


    }

    @Test
    public void callbackQueryConstantCheck_ReturnsOkResponseForEachDataWithDogShelter() {
        dataForConstantCallBackQueries.forEach(data -> {
            SendResponse testingSendResponse =
                    out.callBackQueryConstantCheck(
                            BotUtils.fromJson(
                                    json.replace("%data%",
                                            dogShelterName + data),
                                    CallbackQuery.class), dogShelter);
            assertTrue(testingSendResponse.isOk());
        });
    }

    @Test
    public void shelterMenu_ReturnsOkResponse() {
        SendResponse testingResponse =
                out.shelterMenu(123L, dogShelter);
        assertTrue(testingResponse.isOk());
    }

    @Test
    public void shelterMenu_ThrowsNotReturnedResponseException() {
        assertThrows(NotReturnedResponseException.class, () -> {
            out.shelterMenu(123L,
                    new Shelter("Просто какой-то приют"));
        });
    }

    @Test
    void shouldChooseCatMenu() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(out, 2);

        Method method = CallbackChecker.class.getDeclaredMethod("choosePetMenu", Shelter.class);
        method.setAccessible(true);

        when(shelter.getShelterType()).thenReturn(ShelterType.DOGS_SHELTER);
        method.invoke(out, shelter);
        assertEquals(shelter.getShelterType(), ShelterType.DOGS_SHELTER);
    }

    @Test
    void shouldChooseDogMenu() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(out, 1);

        Method method = CallbackChecker.class.getDeclaredMethod("choosePetMenu", Shelter.class);
        method.setAccessible(true);

        when(shelter.getShelterType()).thenReturn(ShelterType.CATS_SHELTER);
        method.invoke(out, shelter);
        assertEquals(shelter.getShelterType(), ShelterType.CATS_SHELTER);
    }

    @Test
    void shouldInputDogNameFromUser() throws NoSuchFieldException, IllegalAccessException {
        Dog dog = new Dog();
        when(choosePetForPotentialOwnerBlock.getDogByNameFromUserRequest(name, chatId))
                .thenReturn(dog);

        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(out, 1);

        out.inputNameFromUser(chatId, name);
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldInputCatNameFromUser() throws NoSuchFieldException, IllegalAccessException {
        Cat cat = new Cat();
        when(choosePetForPotentialOwnerBlock.getCatByNameFromUserRequest(name, chatId))
                .thenReturn(cat);

        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(out, 2);

        out.inputNameFromUser(chatId, name);
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldInputNameFromUserWhenDefault() throws NoSuchFieldException, IllegalAccessException {

        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(out, 0);

        Field fieldAnimal = CallbackChecker.class.getDeclaredField("animal");
        fieldAnimal.setAccessible(true);
        Animal animal = new Dog();
        fieldAnimal.set(out, animal);

        out.inputNameFromUser(chatId, name);

        assertEquals(field.getInt(out), 0);
        assertNull(fieldAnimal.get(out));
    }
}