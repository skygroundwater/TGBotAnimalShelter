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
import org.springframework.http.HttpStatus;
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

    @PostMapping("/dogs")
    public ResponseEntity<DogReport> postDogReport(@RequestBody DogReport dogReport,
                                                   @RequestParam("images") MultipartFile... multipartFiles) {


        return ResponseEntity.ok(dogReportService.postReport(dogReport, multipartFiles));
    }

    @PostMapping("/cats")
    public ResponseEntity<CatReport> postCatReport(@RequestBody CatReport catReport) {
        return ResponseEntity.ok(catReportService.postReport(catReport));
    }

    @GetMapping
    public ResponseEntity<List<? extends Report>> findReportsByPet(@RequestBody T animal) {
        if (animal instanceof Cat) {
            return ResponseEntity.ok(catReportService.findReportsFromPet((Cat) animal));
        } else if (animal instanceof Dog) {
            return ResponseEntity.ok(dogReportService.findReportsFromPet((Dog) animal));
        } else throw new NotValidDataException("Не валидно");
    }

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
