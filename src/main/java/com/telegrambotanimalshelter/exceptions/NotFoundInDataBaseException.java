package com.telegrambotanimalshelter.exceptions;

public class NotFoundInDataBaseException extends RuntimeException {
    public NotFoundInDataBaseException(String description) {
        super(description);
    }
}
