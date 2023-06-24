package com.telegrambotanimalshelter.exceptions;


public class NotValidDataException extends RuntimeException {
    public NotValidDataException(String description) {
        super(description);
    }
}
