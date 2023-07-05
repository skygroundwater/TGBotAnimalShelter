package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.dto.animals.DogDTO;
import com.telegrambotanimalshelter.exceptions.FileProcessingException;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petphotoservice.PetPhotoService;
import com.telegrambotanimalshelter.services.petservice.PetService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static com.telegrambotanimalshelter.services.mapper.DogMapper.convertToDog;

@RestController
@RequestMapping("/dogs")
public class DogController {

    private final PetService<Dog> dogsService;

    private final ModelMapper modelMapper;

    private final PetPhotoService petPhotoService;

    public DogController(@Qualifier("dogsServiceImpl") PetService<Dog> dogsService, ModelMapper modelMapper, PetPhotoService petPhotoService) {
        this.dogsService = dogsService;
        this.modelMapper = modelMapper;
        this.petPhotoService = petPhotoService;
    }

    @PostMapping
    public ResponseEntity<Dog> postDog(@RequestBody DogDTO dogDTO) {
        return ResponseEntity.ok(dogsService.postPet(convertToDog(dogDTO, modelMapper)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dog> findDog(@PathVariable Long id) {
        return ResponseEntity.ok(dogsService.findPet(id));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteDog(@RequestBody DogDTO dogDTO) {
        return ResponseEntity.ok(dogsService.deletePet(convertToDog(dogDTO, modelMapper)));
    }

    @PutMapping("/put")
    public ResponseEntity<Dog> putDog(@RequestBody DogDTO dogDTO) {
        return ResponseEntity.ok(dogsService.putPet(convertToDog(dogDTO, modelMapper)));
    }

    @PutMapping(name = "/photo/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Dog> uploadPetPhoto(@RequestParam String name,
                                              @RequestParam MultipartFile file) {

        String originalFilename = file.getOriginalFilename();

        File dataFile = petPhotoService.getDataFile(name + "_photo_" + originalFilename);
        petPhotoService.createNewFileIfNotExist(dataFile.getName());


        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
             FileOutputStream fos = new FileOutputStream(dataFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] buffer = new byte[1024];
            while (bis.read(buffer) > 0) {
                bos.write(buffer);
            }

            dogsService.setPhoto(name, file.getBytes());
            return ResponseEntity.ok()
                    .contentLength(dataFile.length())
                    .build();

        } catch (IOException e) {
            throw new FileProcessingException("Ошибка загрузки файла");
        }
    }
}