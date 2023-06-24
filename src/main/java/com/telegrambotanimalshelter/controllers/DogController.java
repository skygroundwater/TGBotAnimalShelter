package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petservice.PetService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dogs")
public class DogController {

    private final PetService<Dog> dogsService;

    public DogController(@Qualifier("dogsServiceImpl") PetService<Dog> dogsService) {
        this.dogsService = dogsService;
    }

    @PostMapping
    public ResponseEntity<Dog> postDog(@RequestBody Dog dog) {
        return ResponseEntity.ok(dogsService.postPet(dog));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dog> findDog(@PathVariable Long id) {
        return ResponseEntity.ok(dogsService.findPet(id));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteDog(@RequestBody Dog dog) {
        return ResponseEntity.ok(dogsService.deletePet(dog));
    }

    @PutMapping("/put")
    public ResponseEntity<Dog> putDog(@RequestBody Dog dog) {
        return ResponseEntity.ok(dogsService.putPet(dog));
    }
}