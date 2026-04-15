package id.ac.ui.cs.advprog.mysawit.harvest.exception;

import id.ac.ui.cs.advprog.mysawit.harvest.service.HarvestServiceImpl.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HarvestAlreadySubmittedException.class)
    public ResponseEntity<Map<String, String>> handleAlreadySubmitted(HarvestAlreadySubmittedException e) {
        return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(HarvestNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(HarvestNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedMandorException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedMandorException e) {
        return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransition(InvalidStatusTransitionException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArg(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }
}