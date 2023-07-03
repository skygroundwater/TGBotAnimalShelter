package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.models.animals.Animal;
import com.telegrambotanimalshelter.models.animals.Cat;
import com.telegrambotanimalshelter.models.animals.Dog;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.models.reports.Report;
import com.telegrambotanimalshelter.services.reportservice.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController<N extends Report, T extends Animal> {

    private final ReportService<DogReport, Dog, DogImage> dogReportService;

    private final ReportService<CatReport, Cat, CatImage> catReportService;

    public ReportController(ReportService<DogReport, Dog, DogImage> dogReportService,
                            ReportService<CatReport, Cat, CatImage> catReportService) {
        this.dogReportService = dogReportService;
        this.catReportService = catReportService;
    }

    @Operation(
            summary = "Поиск отчета",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отчет найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчет не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            },
            tags = "Отчеты"
    )
    @GetMapping
    public ResponseEntity<List<? extends Report>> findReportsByPet(@RequestBody T animal) {
        if (animal instanceof Cat) {
            return ResponseEntity.ok(catReportService.findReportsFromPet((Cat) animal));
        } else if (animal instanceof Dog) {
            return ResponseEntity.ok(dogReportService.findReportsFromPet((Dog) animal));
        } else throw new NotValidDataException("Не валидно");
    }

    @Operation(
            summary = "Добавление нового отчета по собакам в БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новый отчет по собаке"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отчет добавлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DogReport.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчет не удалось добавить в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DogReport.class))
                            )
                    )
            },
            tags = "Отчеты"
    )
    @PostMapping("/dogs")
    public ResponseEntity<DogReport> postDogReport(@RequestBody DogReport dogReport,
                                                   @RequestParam("images") MultipartFile... multipartFiles) {


        return ResponseEntity.ok(dogReportService.postReport(dogReport, multipartFiles));
    }

    @Operation(
            summary = "Добавление нового отчета по кошкам в БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Новый отчет по кошке"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отчет добавлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CatReport.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчет не удалось добавить в БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CatReport.class))
                            )
                    )
            },
            tags = "Отчеты"
    )
    @PostMapping("/cats")
    public ResponseEntity<CatReport> postCatReport(@RequestBody CatReport catReport) {
        return ResponseEntity.ok(catReportService.postReport(catReport));
    }

    @Operation(
            summary = "Удаление отчета из БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Удаляемый отчет"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отчет удален",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчет не удалось убрать из БД",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            },
            tags = "Отчеты"
    )
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteReportsByPet(T animal) {
        if (animal instanceof Cat) {
            return ResponseEntity.ok(catReportService.deleteReportsByPet((Cat) animal));
        } else if (animal instanceof Dog) {
            return ResponseEntity.ok(dogReportService.deleteReportsByPet((Dog) animal));
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
