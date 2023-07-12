package com.telegrambotanimalshelter.listener.parts.requests;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import com.telegrambotanimalshelter.services.petphotoservice.PetPhotoService;
import com.telegrambotanimalshelter.services.petservice.CatsServiceImpl;
import com.telegrambotanimalshelter.services.petservice.DogsServiceImpl;
import com.telegrambotanimalshelter.utils.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChoosePetForPotentialOwnerBlockTest<A extends Animal, R extends Report> {

    String name = "Name";
    Long chatId = 123L;
    @Mock
    MessageSender<A> sender;
    @Mock
    CatsServiceImpl catsService;
    @Mock
    DogsServiceImpl dogsService;
    @Mock
    CatsRepository catsRepository;
    @Mock
    DogsRepository dogsRepository;
    @Mock
    PetOwnersService petOwnersService;
    @Mock
    PetPhotoService petPhotoService;
    List<Cat> cats;
    List<Dog> dogs;

    @InjectMocks
    ChoosePetForPotentialOwnerBlock<A, R> choosePetForPotentialOwnerBlock;

    @Test
    void shouldGetDogByNameFromUserRequest() {
        Dog dog = new Dog();
        dog.setNickName(name);

        when(dogsRepository.findDogsByNickName(name)).thenReturn(dog);
        assertEquals(dog, choosePetForPotentialOwnerBlock.getDogByNameFromUserRequest(name, chatId));
    }

    @Test
    void shouldThrowExceptionWhenNotFoundDogByNameFromUserRequest() {

        when(dogsRepository.findDogsByNickName(name)).thenReturn(null);

        NotFoundInDataBaseException exception = assertThrows(NotFoundInDataBaseException.class,
                () -> choosePetForPotentialOwnerBlock.getDogByNameFromUserRequest(name, chatId));
        assertEquals("Собаки с таким именем нет в базе или задан неверный запрос", exception.getMessage());
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldGetCatByNameFromUserRequest() {
        Cat cat = new Cat();
        cat.setNickName(name);

        when(catsRepository.findCatsByNickName(name)).thenReturn(cat);
        assertEquals(cat, choosePetForPotentialOwnerBlock.getCatByNameFromUserRequest(name, chatId));
    }

    @Test
    void shouldThrowExceptionWhenNotFoundCatByNameFromUserRequest() {
        when(catsRepository.findCatsByNickName(name)).thenReturn(null);

        NotFoundInDataBaseException exception = assertThrows(NotFoundInDataBaseException.class,
                () -> choosePetForPotentialOwnerBlock.getCatByNameFromUserRequest(name, chatId));
        assertEquals("Кошки с таким именем нет в базе или задан неверный запрос", exception.getMessage());
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldGetAnimalInfoIfExist() {
        Animal animal = new Dog();
        animal.setAbout("about");

        choosePetForPotentialOwnerBlock.getAnimalInfo(animal, chatId);
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldGetAnimalInfoIfNotExist() {
        Animal animal = new Dog();

        choosePetForPotentialOwnerBlock.getAnimalInfo(animal, chatId);
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldGetAnimalInfoIfNull() {

        choosePetForPotentialOwnerBlock.getAnimalInfo(null, chatId);
        verify(sender).sendResponse(any(SendMessage.class));
    }

    @Test
    void shouldGetPetPhotoFromShelter() throws URISyntaxException {
        Animal animal = new Dog();
        animal.setNickName("animal");
        when(petPhotoService.getPetPhoto(animal, animal.getNickName()))
                .thenReturn(new File(ChoosePetForPotentialOwnerBlockTest.class.getResource("photo").toURI()));
        choosePetForPotentialOwnerBlock.getPetPhotoFromShelter(animal, chatId);
        verify(sender).sendResponse(any(SendPhoto.class));

    }

    @Test
    void shouldGetPetPhotoFromShelterIfPhotoIsNull() {
        Animal animal = new Dog();
        animal.setNickName("animal");
        when(petPhotoService.getPetPhoto(animal, animal.getNickName()))
                .thenReturn(null);
        choosePetForPotentialOwnerBlock.getPetPhotoFromShelter(animal, chatId);
        verify(sender).sendResponse(any(SendMessage.class));

    }
}