package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.models.PetOwner;
import com.telegrambotanimalshelter.services.petownerservice.PetOwnersService;
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
@RequestMapping("/pet_owners")
public class PetOwnerController {

    private final PetOwnersService petOwnersService;

    public PetOwnerController(PetOwnersService petOwnersService) {
        this.petOwnersService = petOwnersService;
    }

    @Operation(
            summary = "Добавление нового владельца в БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новый владелец"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Владелец добавлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Владельца не удалось добавить в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    )
            },
            tags = "Владельцы животных"
    )
    @PostMapping
    public ResponseEntity<PetOwner> postPetOwner(@RequestBody PetOwner petOwner) {
        return ResponseEntity.ok(petOwnersService.postPetOwner(petOwner));
    }

    @Operation(
            summary = "Поиск владельца по его id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный владелец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Владелец не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    )
            },
            tags = "Владельцы животных"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PetOwner> findPetOwner(@Parameter(description = "id владельца") @PathVariable Long id) {
        return ResponseEntity.ok(petOwnersService.findPetOwner(id));
    }

    @Operation(
            summary = "Изменение параметров владельца",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Редактируемый владелец"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Владелец изменен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Владельца не удалось изменить",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    )
            },
            tags = "Владельцы животных"
    )
    @PutMapping
    public ResponseEntity<PetOwner> putPetOwner(@RequestBody PetOwner petOwner) {
        return ResponseEntity.ok(petOwnersService.putPetOwner(petOwner));
    }

    @Operation(
            summary = "Удаление владельца из БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Удаляемый владелец"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Владелец удален",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Владельца не удалось убрать из БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PetOwner.class))
                            )
                    )
            },
            tags = "Владельцы животных"
    )
    @DeleteMapping
    public ResponseEntity<HttpStatus> deletePetOwner(@RequestBody PetOwner petOwner) {
        return ResponseEntity.ok(petOwnersService.deletePetOwner(petOwner));
    }
}