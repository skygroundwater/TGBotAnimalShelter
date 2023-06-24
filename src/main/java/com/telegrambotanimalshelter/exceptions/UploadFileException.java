package com.telegrambotanimalshelter.exceptions;

public class UploadFileException extends RuntimeException {

    public UploadFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UploadFileException(String msg) {
        super(msg);
    }

    public UploadFileException(Throwable cause) {
        super(cause);
    }
}
