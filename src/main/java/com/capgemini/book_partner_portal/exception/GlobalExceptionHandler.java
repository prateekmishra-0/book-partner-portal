package com.capgemini.book_partner_portal.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch specific "Not Found" exceptions from Spring Data REST
    @ExceptionHandler(org.springframework.data.rest.webmvc.ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(Exception e) {
        return ResponseEntity.status(404)
                .body("The requested resource was not found: " + e.getMessage());
    }

    // Catch Spring Data REST validation errors and return 400 Bad Request
    @ExceptionHandler(org.springframework.data.rest.core.RepositoryConstraintViolationException.class)
    public ResponseEntity<String> handleRepositoryValidationException(Exception e) {
        return ResponseEntity.status(400)
                .body("Validation failed: Invalid input data.");
    }

    // Catch Standard Hibernate validation errors and return 400 Bad Request
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(Exception e) {
        return ResponseEntity.status(400)
                .body("Validation failed: " + e.getMessage());
    }

    // Catch attempts to overwrite existing records via POST
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<String> handleResourceAlreadyExists(Exception e) {
        // 409 Conflict is the REST standard for "This ID is already taken"
        return ResponseEntity.status(409)
                .body("Conflict: " + e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {

        return ResponseEntity.status(500)
                .body(e.getMessage());
    }

}
