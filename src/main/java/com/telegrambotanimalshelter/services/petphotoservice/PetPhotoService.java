package com.telegrambotanimalshelter.services.petphotoservice;

import com.telegrambotanimalshelter.exceptions.FileProcessingException;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.services.petservice.CatsServiceImpl;
import com.telegrambotanimalshelter.services.petservice.DogsServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PetPhotoService {


    private final String dataTempFilePath = "src/main/resources/tempfiles";

    private final CatsServiceImpl catsService;
    private final DogsServiceImpl dogsService;

    public PetPhotoService(CatsServiceImpl catsService, DogsServiceImpl dogsService) {
        this.catsService = catsService;
        this.dogsService = dogsService;
    }

    @PostConstruct
    private void init() {
        Path path = Path.of(dataTempFilePath);
        try {

            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }

        } catch (IOException e) {
            throw new FileProcessingException("Проблема с чтением из файла");
        }
    }

    public File getDataFile(String dataFileName) {
        return new File(dataTempFilePath + "/" + dataFileName);
    }

    public void cleanDataFile(String dataFileName) {
        try {
            Path path = Path.of(dataTempFilePath, dataFileName);
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNewFileIfNotExist(String dataFileName) {
        try {
            Path path = Path.of(dataTempFilePath, dataFileName);
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getPetPhoto(Animal animal, String name) {
        byte[] photo;
        if (animal instanceof Cat) {
            photo = catsService.getPhoto(name);
        } else {
            photo = dogsService.getPhoto(name);
        }
        File dataFile = new File(dataTempFilePath + "/" + name + "_photo");

        if (photo != null) {
            try (FileOutputStream fos = new FileOutputStream(dataFile)) {
                fos.write(photo);
            } catch (IOException e) {
                throw new FileProcessingException("Ошибка загрузки файла");
            }
            return dataFile;
        } else
            return null;
    }
}
