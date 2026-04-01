package com.capgemini.book_partner_portal.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch specific "Not Found" exceptions from Spring Data REST
    @ExceptionHandler(org.springframework.data.rest.webmvc.ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("The requested record could not be found. It may have been deleted or deactivated.");
    }

    // Catch Spring Data REST validation errors and return 400 Bad Request
    @ExceptionHandler(RepositoryConstraintViolationException.class)
    public ResponseEntity<?> handleValidationErrors(RepositoryConstraintViolationException ex) {
        // Extract the specific field errors
        Map<String, String> errors = ex.getErrors().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Catch Standard Hibernate validation errors and return 400 Bad Request
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Data validation failed. Please check your inputs and try again.");
    }

    // Catch attempts to overwrite existing records via POST (Custom Logic)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<String> handleResourceAlreadyExists(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage()); // Assuming your custom exception passes a clean message
    }

    // NEW: Catch Database-level duplicate key / integrity errors
    // This perfectly handles the ghost ID scenario without throwing a 500 crash!
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("A record with this unique identifier already exists in the database.");
    }

    // Generic Fallback (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // 1. Print the ugly technical error to the backend console so YOU can debug it
        e.printStackTrace();

        // 2. Send a safe, polite message to the frontend UI
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected server error occurred. Please contact system support.");
    }
}