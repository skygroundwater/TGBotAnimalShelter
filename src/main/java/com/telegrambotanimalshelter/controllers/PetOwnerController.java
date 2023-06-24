package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pet_owners")
public class PetOwnerController {

    private final PetOwnersService petOwnersService;

    public PetOwnerController(PetOwnersService petOwnersService) {
        this.petOwnersService = petOwnersService;
    }

    @PostMapping
    public ResponseEntity<PetOwner> postPetOwner(@RequestBody PetOwner petOwner) {
        return ResponseEntity.ok(petOwnersService.postPetOwner(petOwner));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetOwner> findPetOwner(@PathVariable Long id) {
        return ResponseEntity.ok(petOwnersService.findPetOwner(id));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deletePetOwner(@RequestBody PetOwner petOwner) {
        return ResponseEntity.ok(petOwnersService.deletePetOwner(petOwner));
    }

    @PutMapping
    public ResponseEntity<PetOwner> putPetOwner(@RequestBody PetOwner petOwner) {
        return ResponseEntity.ok(petOwnersService.putPetOwner(petOwner));
    }
}