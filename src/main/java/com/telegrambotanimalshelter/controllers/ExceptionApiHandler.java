package com.telegrambotanimalshelter.controllers;

import com.telegrambotanimalshelter.exceptions.EmptyDTOException;
import com.telegrambotanimalshelter.exceptions.NotFoundInDataBaseException;
import com.telegrambotanimalshelter.exceptions.NotValidDataException;
import com.telegrambotanimalshelter.exceptions.UploadFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(UploadFileException.class)
    public ResponseEntity<String> fileProcessingException(UploadFileException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(NotValidDataException.class)
    public ResponseEntity<String> BadRequestException(NotValidDataException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(NotFoundInDataBaseException.class)
    public ResponseEntity<String> modelNotFoundException(NotFoundInDataBaseException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(EmptyDTOException.class)
    public ResponseEntity<String> modelNotFoundException(EmptyDTOException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
