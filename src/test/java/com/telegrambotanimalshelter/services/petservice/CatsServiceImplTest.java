package com.telegrambotanimalshelter.services.petservice;

import com.telegrambotanimalshelter.exceptions.FileProcessingException;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.models.Shelter;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.repositories.animals.CatsRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatsServiceImplTest {

    @InjectMocks
    CatsServiceImpl catsService;
    @Mock
    CatsRepository catsRepository;
    @Mock
    Shelter shelter;
    Cat cat = new Cat();
    PetOwner petOwner = new PetOwner();

    @Test
    void postPet() {
        when(catsRepository.save(cat)).thenReturn(cat);
        assertEquals(catsService.postPet(cat), cat);
    }

    @Test
    void deletePet() {
        assertEquals(catsService.deletePet(cat), HttpStatus.OK);
    }

    @Test
    void findPet() {
        when(catsRepository.findById(1L)).thenReturn(Optional.ofNullable(cat));
        assertEquals(catsService.findPet(1L), cat);
        assertThrows(NotFoundInDataBaseException.class, () -> catsService.findPet(0L));
    }

    @Test
    void putPet() {
        when(catsRepository.save(cat)).thenReturn(cat);
        assertEquals(catsService.putPet(cat), cat);
        assertThrows(NotValidDataException.class, () -> catsService.putPet(null));
    }

    @Test
    void setPetOwner() {
        cat.setSheltered(true);
        cat.setPetOwner(petOwner);
        when(catsRepository.save(cat)).thenReturn(cat);
        assertEquals(catsService.setPetOwner(cat, petOwner), cat);
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
        when(catsRepository.findCatsByNickName("name")).thenReturn(cat);
        when(catsRepository.save(cat)).thenReturn(cat);
        cat.setNickName("name");
        cat.setPhoto(photo);
        catsService.setPhoto("name", photo);
        verify(catsRepository).findCatsByNickName(any());
        verify(catsRepository).save(any());

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
        cat.setPhoto(photo);
        when(catsRepository.findCatsByNickName("name")).thenReturn(cat);
        assertEquals(catsService.getPhoto("name").length, photo.length);
    }

    @Test
    void findPetByName() {
        when(catsRepository.findCatsByNickName("name")).thenReturn(cat);
        assertEquals(catsService.findPetByName("name"), cat);
    }

    @Test
    void findPetsByPetOwner() {
        List<Cat> cats = List.of(cat);
        when(catsRepository.findCatsByPetOwner(petOwner)).thenReturn(cats);
        assertEquals(catsService.findPetsByPetOwner(petOwner), cats);
    }

    @Test
    void getAllPets() {
        List<Cat> cats = List.of(cat);
        when(catsRepository.findAll()).thenReturn(cats);
        assertEquals(catsService.getAllPets(), cats);
    }

    @Test
    void getShelter() {
        assertEquals(catsService.getShelter(), shelter);
    }
}