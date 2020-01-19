package com.splitter.userservice.exception;

public class RecordConflictException extends RuntimeException {

    public RecordConflictException(String message) {
        super(message);
    }
}
