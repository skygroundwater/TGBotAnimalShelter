package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.services.volunteerservice.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @Operation(
            summary = "Добавление нового волонтера в БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новый волонтер"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Волонтер добавлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Волонтера не удалось добавить в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            tags = "Волонтеры"
    )
    @PostMapping
    public ResponseEntity<Volunteer> postVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.saveVolunteer(volunteer));
    }

    @Operation(
            summary = "Поиск волонтера по его id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный волонтер",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Волонтер не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            tags = "Волонтеры"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> findVolunteer(@Parameter(description = "id волонтера") @PathVariable Long id){
        return ResponseEntity.ok(volunteerService.findVolunteer(id));
    }

    @Operation(
            summary = "Изменение параметров волонтера",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Редактируемый волонтер"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Волонтер изменен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Волонтера не удалось изменить",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            tags = "Волонтеры"
    )
    @PutMapping
    public ResponseEntity<Volunteer> putVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.putVolunteer(volunteer));
    }

    @Operation(
            summary = "Удаление волонтера из БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Удаляемый владелец"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Волонтера удален",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Волонтера не удалось убрать из БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            tags = "Волонтеры"
    )
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.deleteVolunteer(volunteer));
    }
}
