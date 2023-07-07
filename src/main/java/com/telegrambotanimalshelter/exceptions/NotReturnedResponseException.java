package com.telegrambotanimalshelter.exceptions;

public class NotReturnedResponseException extends RuntimeException{

    public NotReturnedResponseException(){
        super("Ответ не был сформирован");
    }
}
