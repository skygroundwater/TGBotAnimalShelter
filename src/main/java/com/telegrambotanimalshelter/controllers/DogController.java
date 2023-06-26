package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.dto.animals.DogDTO;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petservice.PetService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.telegrambotanimalshelter.services.mapper.DogMapper.convertToDog;

@RestController
@RequestMapping("/dogs")
public class DogController {

    private final PetService<Dog> dogsService;

    private final ModelMapper modelMapper;

    public DogController(@Qualifier("dogsServiceImpl") PetService<Dog> dogsService, ModelMapper modelMapper) {
        this.dogsService = dogsService;
        this.modelMapper = modelMapper;
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
}