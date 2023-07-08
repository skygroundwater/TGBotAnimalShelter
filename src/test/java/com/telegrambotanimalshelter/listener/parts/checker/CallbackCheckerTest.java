package com.telegrambotanimalshelter.listener.parts.checker;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.telegrambotanimalshelter.enums.ShelterType;
import com.telegrambotanimalshelter.exceptions.NotReturnedResponseException;
import com.telegrambotanimalshelter.listener.parts.BecomingPetOwnerPart;
import com.telegrambotanimalshelter.listener.parts.IntroductionPart;
import com.telegrambotanimalshelter.listener.parts.keeper.CacheKeeper;
import com.telegrambotanimalshelter.listener.parts.requests.ChoosePetForPotentialOwnerBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ContactRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.ReportRequestBlock;
import com.telegrambotanimalshelter.listener.parts.requests.VolunteerAndPetOwnerChat;
import com.telegrambotanimalshelter.listener.parts.volunteerblock.VolunteerBlock;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.Volunteer;
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.telegrambotanimalshelter.utils.Constants.catShelterName;
import static com.telegrambotanimalshelter.utils.Constants.dogShelterName;
import static com.telegrambotanimalshelter.utils.ConstantsForTesting.petOwner1;
import static com.telegrambotanimalshelter.utils.ConstantsForTesting.volunteer1;
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

    @Mock
    private CacheKeeper<A, R> keeper;

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

    final List<String> dataForConstantCallBackQueries =
            new ArrayList<>(List.of("_shelter_info",
                    "_info", "_hours", "_pass", "_safety",
                    "_shelter_consultation", "_acquaintance",
                    "_documents", "_transportation",
                    "_little", "_adult", "_restricted",
                    "_reasons_for_refusal"));

    final String json = Files.readString(Path.of(CallbackCheckerTest.class
            .getResource("callbackquery.json").toURI()));

    CallbackCheckerTest() throws IOException, URISyntaxException {
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

        when(introductionPart.welcome(chatId, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterPass(chatId, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterInfo(chatId, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterWorkingHours(chatId, dogShelter)).thenReturn(okSendResponse);
        when(introductionPart.shelterSafety(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.welcome(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.documentsForPetOwner(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.acquaintanceWithPet(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.transportation(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.homeForLittlePet(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.homeForAdultPet(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.homeForRestrictedPet(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.reasonsForRefusal(chatId, dogShelter)).thenReturn(okSendResponse);
        when(becomingPart.firstMeetingWithDog(chatId, dogShelter)).thenReturn(okSendResponse);

        when(sender.sendResponse(any(SendMessage.class))).thenReturn(okSendResponse);

        when(sender.sendStartMessage(123L)).thenReturn(okSendResponse);


    }

    @Test
    void callBackQueryCheck_ReturnsPetOwnerWhoIsIntoTheContactBlock() {
        //given
        PetOwner petOwner = petOwner1;
        petOwner.setContactRequest(true);
        when(contactBlock.sendMessageToTakeName(chatId)).thenReturn(petOwner);
        //when
        PetOwner testingPetOwner = (PetOwner) out.callbackQueryCheck(
                BotUtils.fromJson(
                        json.replace("%data%",
                                "_contacts"),
                        CallbackQuery.class)).get();
        //then
        assertEquals(testingPetOwner.isReportRequest(), petOwner.isReportRequest());
        assertEquals(testingPetOwner.isContactRequest(), petOwner.isContactRequest());
    }

    @Test
    public void callBackQueryCheck_ReturnsPetOwnerWhoIsIntoTheRequestBlock() {
        //given
        PetOwner petOwner = petOwner1;
        petOwner.setReportRequest(true);
        when(reportRequestBlock.startReportFromPetOwner(chatId))
                .thenReturn(petOwner);
        //when
        PetOwner testingPetOwner =
                (PetOwner) out.callbackQueryCheck(
                        BotUtils.fromJson(
                                json.replace("%data%",
                                        "_report"),
                                CallbackQuery.class)).get();
        //then
        assertEquals(testingPetOwner.isReportRequest(), petOwner.isReportRequest());
        assertEquals(testingPetOwner.isContactRequest(), petOwner.isContactRequest());
    }

    @Test
    public void callBackQueryCheck_ReturnsVolunteerWhoIsInOfficeButNotIsCheckingReports() {
        //given
        Volunteer volunteer = volunteer1;
        volunteer.setFree(false);
        volunteer.setInOffice(true);
        when(volunteerBlock.startWorkWithVolunteer(chatId))
                .thenReturn(volunteer);
        //when
        Volunteer testingVolunteer =
                (Volunteer) out.callbackQueryCheck(
                        BotUtils.fromJson(
                                json.replace("%data%",
                                        "i_am_volunteer"),
                                CallbackQuery.class)).get();
        //then
        assertEquals(testingVolunteer, volunteer);
    }

    @Test
    public void callBackQueryCheck_ReturnsOkResponse(){

        //when
        SendResponse sendResponse =
                (SendResponse) out.callbackQueryCheck(
                        BotUtils.fromJson(
                                json.replace("%data%",
                                        dogShelterName + "_first_meeting"),
                                CallbackQuery.class)).get();

        //then
        assertEquals(sendResponse.isOk(), okSendResponse.isOk());

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