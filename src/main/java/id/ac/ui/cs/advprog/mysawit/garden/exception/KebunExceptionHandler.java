package id.ac.ui.cs.advprog.mysawit.garden.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.mysawit.garden")
public class KebunExceptionHandler {

    @ExceptionHandler(KebunNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(KebunNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateKodeKebunException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKode(DuplicateKodeKebunException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(KebunHasMandorException.class)
    public ResponseEntity<Map<String, Object>> handleHasMandor(KebunHasMandorException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(KebunOverlapException.class)
    public ResponseEntity<Map<String, Object>> handleOverlap(KebunOverlapException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(InvalidKebunPolygonException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPolygon(InvalidKebunPolygonException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MandorAlreadyAssignedException.class)
    public ResponseEntity<Map<String, Object>> handleMandorAlreadyAssigned(MandorAlreadyAssignedException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SupirAlreadyAssignedException.class)
    public ResponseEntity<Map<String, Object>> handleSupirAlreadyAssigned(SupirAlreadyAssignedException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> {
                    Map<String, String> errorDetail = new LinkedHashMap<>();
                    errorDetail.put("field", err.getField());
                    errorDetail.put("message", err.getDefaultMessage());
                    return errorDetail;
                })
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "error");
        body.put("message", "Validasi gagal");
        body.put("errors", fieldErrors);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "error");
        body.put("message", message);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}
