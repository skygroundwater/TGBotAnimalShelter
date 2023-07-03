package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.dto.animals.CatDTO;
import com.telegrambotanimalshelter.exceptions.FileProcessingException;
import com.telegrambotanimalshelter.models.animals.Cat;
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

import static com.telegrambotanimalshelter.services.mapper.CatMapper.convertToCat;

@RestController
@RequestMapping("/cats")
public class CatController {

    private final PetService<Cat> catsService;
    private final ModelMapper modelMapper;

    private final PetPhotoService petPhotoService;

    public CatController(@Qualifier("catsServiceImpl") PetService<Cat> catsService, ModelMapper modelMapper, PetPhotoService petPhotoService) {
        this.catsService = catsService;
        this.modelMapper = modelMapper;
        this.petPhotoService = petPhotoService;
    }

    @PostMapping
    public ResponseEntity<Cat> postCat(@RequestBody CatDTO catDTO) {
        return ResponseEntity.ok(catsService.postPet(convertToCat(catDTO, modelMapper)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cat> findCat(@PathVariable Long id) {
        return ResponseEntity.ok(catsService.findPet(id));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteCat(@RequestBody CatDTO catDTO) {
        return ResponseEntity.ok(catsService.deletePet(convertToCat(catDTO, modelMapper)));
    }

    @PutMapping("/put")
    public ResponseEntity<Cat> putCat(@RequestBody CatDTO catDTO) {
        return ResponseEntity.ok(catsService.putPet(convertToCat(catDTO, modelMapper)));
    }

    @PutMapping(name = "/photo/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Cat> uploadPetPhoto(@RequestParam String name,
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

            catsService.setPhoto(name, file.getBytes());
            return ResponseEntity.ok()
                    .contentLength(dataFile.length())
                    .build();

        } catch (IOException e) {
            throw new FileProcessingException("Ошибка загрузки файла");
        }
    }
}