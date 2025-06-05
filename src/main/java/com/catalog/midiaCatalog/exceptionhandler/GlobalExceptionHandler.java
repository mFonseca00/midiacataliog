package com.catalog.midiacatalog.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.exception.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException exception){
        ErrorResponse errorResponse = new ErrorResponse("Validation Error", exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException exception){
        ErrorResponse errorResponse = new ErrorResponse("Data Not Found", exception.getMessage());
        return ResponseEntity.status(404).body(errorResponse);
    }
}
