package com.assesment.maybank.spring_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        int code = 1000;
        ApiErrorResponse error = new ApiErrorResponse("USER_NOT_FOUND", ex.getMessage(), code, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(PlaceNotFoundException ex) {
        int code = 2000;
        ApiErrorResponse error = new ApiErrorResponse("PLACE_NOT_FOUND", ex.getMessage(), code, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        int code = 3000;
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("Validation failed");

        ApiErrorResponse error = new ApiErrorResponse("VALIDATION_ERROR", message, code, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        int code = 3001;
        ApiErrorResponse error = new ApiErrorResponse("MISSING_PARAMETER", ex.getMessage(), code, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        int code = 3002;
        ApiErrorResponse error = new ApiErrorResponse("ILLEGAL_ARGUMENT", ex.getMessage(), code, ZonedDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception ex) {
        int code = 5000;
        ApiErrorResponse error = new ApiErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), code,
                ZonedDateTime.now());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
