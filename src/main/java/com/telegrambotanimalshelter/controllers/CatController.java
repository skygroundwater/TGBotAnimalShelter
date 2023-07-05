package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.dto.animals.CatDTO;
import com.telegrambotanimalshelter.exceptions.FileProcessingException;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.services.petphotoservice.PetPhotoService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static com.telegrambotanimalshelter.services.mapper.CatMapper.convertToCat;

@RestController
@RequestMapping("/cats")
public class CatController {

    private final PetService<Cat> catsService;
    private final ModelMapper modelMapper;

    private final PetPhotoService petPhotoService;

    public CatController(@Qualifier("catsServiceImpl") PetService<Cat> catsService, ModelMapper modelMapper, PetPhotoService petPhotoService) {
        this.catsService = catsService;
        this.modelMapper = modelMapper;
        this.petPhotoService = petPhotoService;
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


    @PutMapping(name = "/photo/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Cat> uploadPetPhoto(@RequestParam String name,
                                              @RequestParam MultipartFile file) {

        String originalFilename = file.getOriginalFilename();

        File dataFile = petPhotoService.getDataFile(name + "_photo_" + originalFilename);
        petPhotoService.createNewFileIfNotExist(dataFile.getName());


        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
             FileOutputStream fos = new FileOutputStream(dataFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] buffer = new byte[1024];
            while (bis.read(buffer) > 0) {
                bos.write(buffer);
            }

            catsService.setPhoto(name, file.getBytes());
            return ResponseEntity.ok()
                    .contentLength(dataFile.length())
                    .build();

        } catch (IOException e) {
            throw new FileProcessingException("Ошибка загрузки файла");
        }
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