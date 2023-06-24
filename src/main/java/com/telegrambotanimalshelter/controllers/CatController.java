package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.services.petservice.PetService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cats")
public class CatController {

    private final PetService<Cat> catsService;

    public CatController(@Qualifier("catsServiceImpl") PetService<Cat> catsService) {
        this.catsService = catsService;
    }

    @PostMapping
    public ResponseEntity<Cat> postCat(@RequestBody Cat cat) {
        return ResponseEntity.ok(catsService.postPet(cat));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cat> findCat(@PathVariable Long id) {
        return ResponseEntity.ok(catsService.findPet(id));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteCat(@RequestBody Cat cat) {
        return ResponseEntity.ok(catsService.deletePet(cat));
    }

    @PutMapping("/put")
    public ResponseEntity<Cat> putCat(@RequestBody Cat cat) {
        return ResponseEntity.ok(catsService.putPet(cat));
    }
}