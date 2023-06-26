package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.dto.animals.CatDTO;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.services.petservice.PetService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.telegrambotanimalshelter.services.mapper.CatMapper.convertToCat;

@RestController
@RequestMapping("/cats")
public class CatController {

    private final PetService<Cat> catsService;
    private final ModelMapper modelMapper;

    public CatController(@Qualifier("catsServiceImpl") PetService<Cat> catsService, ModelMapper modelMapper) {
        this.catsService = catsService;
        this.modelMapper = modelMapper;
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
}