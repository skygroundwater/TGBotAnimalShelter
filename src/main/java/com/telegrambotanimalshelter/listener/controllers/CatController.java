package com.telegrambotanimalshelter.listener.controllers;

import com.telegrambotanimalshelter.dto.animals.CatDTO;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.services.petservice.CatsServiceImpl;
import com.telegrambotanimalshelter.services.petservice.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.annotations.Type;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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

    @Operation(
            summary = "Добавление кошки в БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новая кошка"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Кошка добавлена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Кошку не удалось добавить в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    )
            },
            tags = "Кошки"
    )
    @PostMapping
    public ResponseEntity<Cat> postCat(@RequestBody CatDTO catDTO) {
        return ResponseEntity.ok(catsService.postPet(convertToCat(catDTO, modelMapper)));
    }

    @Operation(
            summary = "Поиск кошки по ее id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденная кошка",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Кошку не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    )
            },
            tags = "Кошки"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Cat> findCat(@Parameter(description = "id кошки") @PathVariable Long id) {
        return ResponseEntity.ok(catsService.findPet(id));
    }

    @Operation(
            summary = "Изменение параметров кошки",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Редактируемая кошка"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Кошка изменена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Кошку не удалось изменить",
                            content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    )
            },
            tags = "Кошки"
    )
    @PutMapping("/put")
    public ResponseEntity<Cat> putCat(@RequestBody CatDTO catDTO) {
        return ResponseEntity.ok(catsService.putPet(convertToCat(catDTO, modelMapper)));
    }

    @Operation(
            summary = "Удаление кошки из БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Удаляемая кошка"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Кошка удалена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Кошку не удалось убрать из БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))
                            )
                    )
            },
            tags = "Кошки"
    )
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteCat(@RequestBody CatDTO catDTO) {
        return ResponseEntity.ok(catsService.deletePet(convertToCat(catDTO, modelMapper)));
    }
}