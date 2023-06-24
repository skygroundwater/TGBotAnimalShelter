package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @PostMapping
    public ResponseEntity<Volunteer> postVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.saveVolunteer(volunteer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> findVolunteer(@PathVariable Long id){
        return ResponseEntity.ok(volunteerService.findVolunteer(id));
    }

    @PutMapping
    public ResponseEntity<Volunteer> putVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.putVolunteer(volunteer));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.deleteVolunteer(volunteer));
    }
}
