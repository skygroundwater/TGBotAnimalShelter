package com.telegrambotanimalshelter.services.petservice;

import com.telegrambotanimalshelter.exceptions.FileProcessingException;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.repositories.animals.DogsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogsServiceImplTest {

    @InjectMocks
    DogsServiceImpl dogsService;
    @Mock
    DogsRepository dogsRepository;
    @Mock
    Shelter shelter;
    Dog dog = new Dog();
    PetOwner petOwner = new PetOwner();

    @Test
    void postPet() {
        when(dogsRepository.save(dog)).thenReturn(dog);
        assertEquals(dogsService.postPet(dog), dog);
    }

    @Test
    void deletePet() {
        assertEquals(dogsService.deletePet(dog), HttpStatus.OK);
    }

    @Test
    void findPet() {
        when(dogsRepository.findById(1L)).thenReturn(Optional.ofNullable(dog));
        assertEquals(dogsService.findPet(1L), dog);
        assertThrows(NotFoundInDataBaseException.class, () -> dogsService.findPet(0L));
    }

    @Test
    void putPet() {
        when(dogsRepository.save(dog)).thenReturn(dog);
        assertEquals(dogsService.putPet(dog), dog);
        assertThrows(NotValidDataException.class, () -> dogsService.putPet(null));
    }

    @Test
    void setPetOwner() {
        dog.setSheltered(true);
        dog.setPetOwner(petOwner);
        when(dogsRepository.save(dog)).thenReturn(dog);
        assertEquals(dogsService.setPetOwner(dog, petOwner), dog);
    }

    @Test
    void setPhoto() throws URISyntaxException {
        byte[] photo = new byte[0];
        File dataFile = new File(Path.of(CatsServiceImplTest.class.getResource("photo").toURI()).toUri());

        try (FileOutputStream fos = new FileOutputStream(dataFile)) {
            fos.write(photo);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка загрузки файла");
        }
        when(dogsRepository.findDogByNickName("name")).thenReturn(dog);
        when(dogsRepository.save(dog)).thenReturn(dog);
        dog.setNickName("name");
        dog.setPhoto(photo);
        dogsService.setPhoto("name", photo);
        verify(dogsRepository).findDogByNickName(any());
        verify(dogsRepository).save(any());

    }

    @Test
    void getPhoto() throws URISyntaxException {
        byte[] photo = new byte[0];
        File dataFile = new File(Path.of(CatsServiceImplTest.class.getResource("photo").toURI()).toUri());

        try (FileOutputStream fos = new FileOutputStream(dataFile)) {
            fos.write(photo);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка загрузки файла");
        }
        dog.setPhoto(photo);
        when(dogsRepository.findDogByNickName("name")).thenReturn(dog);
        assertEquals(dogsService.getPhoto("name").length, photo.length);
    }

    @Test
    void findPetByName() {
        when(dogsRepository.findDogByNickName("name")).thenReturn(dog);
        assertEquals(dogsService.findPetByName("name"), dog);
    }

    @Test
    void findPetsByPetOwner() {
        List<Dog> dogs = List.of(dog);
        when(dogsRepository.findDogsByPetOwner(petOwner)).thenReturn(dogs);
        assertEquals(dogsService.findPetsByPetOwner(petOwner), dogs);
    }

    @Test
    void getAllPets() {
        List<Dog> dogs = List.of(dog);
        when(dogsRepository.findAll()).thenReturn(dogs);
        assertEquals(dogsService.getAllPets(), dogs);
    }

    @Test
    void getShelter() {
        assertEquals(dogsService.getShelter(), shelter);
    }
}