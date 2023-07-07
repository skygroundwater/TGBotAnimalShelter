package com.telegrambotanimalshelter.exceptions;

public class CallBackQueryNotRecognizedException extends RuntimeException{

    public CallBackQueryNotRecognizedException(){
        super("Данные с кнопки не опознаны");
    }
}
