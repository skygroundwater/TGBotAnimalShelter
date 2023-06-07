package com.telegrambotanimalshelter.exceptions;

public class AlreadyAddedToDataBaseException extends RuntimeException{

    public AlreadyAddedToDataBaseException(String description){
        super(description);
    }
}
