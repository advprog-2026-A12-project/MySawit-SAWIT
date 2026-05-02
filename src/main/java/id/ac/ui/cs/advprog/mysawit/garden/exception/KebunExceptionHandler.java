package id.ac.ui.cs.advprog.mysawit.garden.exception;

import id.ac.ui.cs.advprog.mysawit.garden.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized exception handler untuk seluruh endpoint Garden module.
 * Menerapkan Single Responsibility Principle — satu class menangani semua error mapping.
 */
@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.mysawit.garden")
public class KebunExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(KebunExceptionHandler.class);

    @ExceptionHandler(KebunNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(KebunNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundInAuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundInAuthException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateKodeKebunException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKode(DuplicateKodeKebunException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MandorAlreadyAssignedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMandorAlreadyAssigned(MandorAlreadyAssignedException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SupirAlreadyAssignedException.class)
    public ResponseEntity<ApiResponse<Void>> handleSupirAlreadyAssigned(SupirAlreadyAssignedException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(KebunHasMandorException.class)
    public ResponseEntity<ApiResponse<Void>> handleHasMandor(KebunHasMandorException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(KebunOverlapException.class)
    public ResponseEntity<ApiResponse<Void>> handleOverlap(KebunOverlapException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(InvalidKebunPolygonException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPolygon(InvalidKebunPolygonException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(InvalidUserRoleException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRole(InvalidUserRoleException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> {
                    Map<String, String> detail = new LinkedHashMap<>();
                    detail.put("field", err.getField());
                    detail.put("message", err.getDefaultMessage());
                    return detail;
                })
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "error");
        body.put("message", "Validation failed");
        body.put("errors", fieldErrors);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error in Garden module", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Terjadi kesalahan internal pada server");
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.error(message));
    }
}
