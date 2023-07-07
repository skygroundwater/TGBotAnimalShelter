package com.telegrambotanimalshelter.listener.parts.checker;

import com.pengrad.telegrambot.request.SendMessage;
import com.telegrambotanimalshelter.enums.ShelterType;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallbackCheckerTest<A extends Animal, R extends Report, I extends AppImage> {

    String name = "Name";
    Long chatId = 123L;

    @Mock
    ContactRequestBlock<A, R> contactBlock;
    @Mock
    ReportRequestBlock<A, R, I> reportRequestBlock;
    @Mock
    VolunteerAndPetOwnerChat<A, R> chat;
    @Mock
    IntroductionPart introductionPart;
    @Mock
    BecomingPetOwnerPart becomingPart;
    @Mock
    Shelter dogShelter;
    @Mock
    Shelter catShelter;
    @Mock
    MessageSender<A> sender;
    @Mock
    VolunteerBlock<A, R, I> volunteerBlock;
    @Mock
    ChoosePetForPotentialOwnerBlock<A, R> choosePetForPotentialOwnerBlock;
    @Mock
    Shelter shelter;

    @InjectMocks
    CallbackChecker<A, R, I> callbackChecker;

    @Test
    void callbackQueryCheck() {
    }

    @Test
    void callBackQueryConstantCheck() {
    }

    @Test
    void shouldChooseCatMenu() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(callbackChecker, 2);

        Method method = CallbackChecker.class.getDeclaredMethod("choosePetMenu", Shelter.class);
        method.setAccessible(true);

        when(shelter.getShelterType()).thenReturn(ShelterType.DOGS_SHELTER);
        method.invoke(callbackChecker, shelter);
        assertEquals(shelter.getShelterType(), ShelterType.DOGS_SHELTER);
    }

    @Test
    void shouldChooseDogMenu() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(callbackChecker, 1);

        Method method = CallbackChecker.class.getDeclaredMethod("choosePetMenu", Shelter.class);
        method.setAccessible(true);

        when(shelter.getShelterType()).thenReturn(ShelterType.CATS_SHELTER);
        method.invoke(callbackChecker, shelter);
        assertEquals(shelter.getShelterType(), ShelterType.CATS_SHELTER);
    }

    @Test
    void shouldInputDogNameFromUser() throws NoSuchFieldException, IllegalAccessException {
        Dog dog = new Dog();
        when(choosePetForPotentialOwnerBlock.getDogByNameFromUserRequest(name, chatId))
                .thenReturn(dog);

        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(callbackChecker, 1);

        callbackChecker.inputNameFromUser(chatId, name);
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldInputCatNameFromUser() throws NoSuchFieldException, IllegalAccessException {
        Cat cat = new Cat();
        when(choosePetForPotentialOwnerBlock.getCatByNameFromUserRequest(name, chatId))
                .thenReturn(cat);

        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(callbackChecker, 2);

        callbackChecker.inputNameFromUser(chatId, name);
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldInputNameFromUserWhenDefault() throws NoSuchFieldException, IllegalAccessException {

        Field field = CallbackChecker.class.getDeclaredField("choosePetMenu");
        field.setAccessible(true);
        field.setInt(callbackChecker, 0);

        Field fieldAnimal = CallbackChecker.class.getDeclaredField("animal");
        fieldAnimal.setAccessible(true);
        Animal animal = new Dog();
        fieldAnimal.set(callbackChecker, animal);

        callbackChecker.inputNameFromUser(chatId, name);

        assertEquals(field.getInt(callbackChecker), 0);
        assertNull(fieldAnimal.get(callbackChecker));
    }
}