package com.telegrambotanimalshelter.services.petphotoservice;

import com.telegrambotanimalshelter.exceptions.FileProcessingException;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petservice.CatsServiceImpl;
import com.telegrambotanimalshelter.services.petservice.DogsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetPhotoServiceTest {

    @InjectMocks
    PetPhotoService petPhotoService;
    @Mock
    CatsServiceImpl catsService;
    @Mock
    DogsServiceImpl dogsService;

    final String dataFileName = "filename";
    private final String dataTempFilePath = "src\\main\\resources\\tempfiles";

    /*@Test
    void getDataFile() {
        Assertions.assertEquals(petPhotoService.getDataFile(dataFileName).getPath(), dataTempFilePath + "\\filename");
    }*/

    @Test
    @Disabled
    void createNewFileIfNotExist() {
    }

    @Test
    void getPetPhotoIfPhotoIsNull() throws URISyntaxException, IOException {
        Animal animal1 = new Cat();
        animal1.setNickName("name");
        Animal animal2 = new Dog();
        animal2.setNickName("name");

        byte[] photo = null;
        Files.readString(Path.of(PetPhotoServiceTest.class.getResource("photo").toURI()));


        when(catsService.getPhoto("name")).thenReturn(null);
        assertNull(petPhotoService.getPetPhoto(animal1, "name"));

        when(dogsService.getPhoto("name")).thenReturn(null);
        assertNull(petPhotoService.getPetPhoto(animal2, "name"));

    }

    @Test
    void getPetPhoto() throws URISyntaxException {
        Animal animal1 = new Cat();
        animal1.setNickName("name");
        Animal animal2 = new Dog();
        animal2.setNickName("name");

        byte[] photo = new byte[0];
        File dataFile = new File(Path.of(PetPhotoServiceTest.class.getResource("photo").toURI()).toUri());

        try (FileOutputStream fos = new FileOutputStream(dataFile)) {
            fos.write(photo);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка загрузки файла");
        }

        animal1.setPhoto(photo);
        animal2.setPhoto(photo);

        when(catsService.getPhoto("name")).thenReturn(photo);
        assertEquals(petPhotoService.getPetPhoto(animal1, "name").length(), dataFile.length());

        when(dogsService.getPhoto("name")).thenReturn(photo);
        assertEquals(petPhotoService.getPetPhoto(animal2, "name").length(), dataFile.length());

    }
}