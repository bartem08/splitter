package com.splitter.userservice.controller;

import com.splitter.userservice.exception.RecordConflictException;
import com.splitter.userservice.exception.ResourceNotFoundException;
import com.splitter.userservice.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private Map<String, String> fieldDescriptions;

    public GlobalExceptionHandler() {
        this.fieldDescriptions = new HashMap<>();
        fieldDescriptions.put("email", "Email");
        fieldDescriptions.put("dateOfBirth", "Date of birth");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.warn(ex.getMessage());
        ErrorResponse responseBody = ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.warn(ex.getMessage());
        FieldError fieldError = ex.getBindingResult().getFieldError();
        ErrorResponse responseBody = ErrorResponse.builder()
                .message(String.format("%s %s %s", fieldDescriptions.get(fieldError.getField()), fieldError.getRejectedValue(), "is invalid."))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordConflictException.class)
    protected ResponseEntity<ErrorResponse> handleRecordConflictException(RecordConflictException ex, WebRequest request) {
        logger.warn(ex.getMessage());
        ErrorResponse responseBody = ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
