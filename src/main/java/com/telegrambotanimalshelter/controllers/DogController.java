package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.dto.animals.DogDTO;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petservice.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation(
            summary = "Добавление собаки в БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новая собака"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Собака добавлена",
                            content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Собаку не удалось добавить в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    )
            },
            tags = "Собаки"
    )
    @PostMapping
    public ResponseEntity<Dog> postDog(@RequestBody DogDTO dogDTO) {
        return ResponseEntity.ok(dogsService.postPet(convertToDog(dogDTO, modelMapper)));
    }

    @Operation(
            summary = "Поиск собаки по ее id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденная собака",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Собака не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    )
            },
            tags = "Собаки"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Dog> findDog(@Parameter(description = "id собаки") @PathVariable Long id) {
        return ResponseEntity.ok(dogsService.findPet(id));
    }

    @Operation(
            summary = "Изменение параметров собаки",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Редактируемая собака"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Собака изменена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Собаку не удалось изменить",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    )
            },
            tags = "Собаки"
    )
    @PutMapping("/put")
    public ResponseEntity<Dog> putDog(@RequestBody DogDTO dogDTO) {
        return ResponseEntity.ok(dogsService.putPet(convertToDog(dogDTO, modelMapper)));
    }

    @Operation(
            summary = "Удаление собаки из БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Удаляемая собака"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Собака удалена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Собаку не удалось убрать из БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Dog.class))
                            )
                    )
            },
            tags = "Собаки"
    )
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteDog(@RequestBody DogDTO dogDTO) {
        return ResponseEntity.ok(dogsService.deletePet(convertToDog(dogDTO, modelMapper)));
    }
}