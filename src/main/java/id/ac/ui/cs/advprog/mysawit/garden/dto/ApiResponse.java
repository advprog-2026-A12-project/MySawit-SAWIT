package id.ac.ui.cs.advprog.mysawit.garden.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Standard API response wrapper sesuai kontrak API MySawit.
 * Semua endpoint Garden mengembalikan format ini.
 *
 * Format:
 * {
 *   "status": "success" | "error",
 *   "message": "...",
 *   "data": { ... } | null,
 *   "timestamp": "2026-03-04T10:00:00Z"
 * }
 *
 * @param <T> tipe data payload
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;
    private String timestamp;

    /**
     * Factory method untuk response sukses.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(Instant.now().toString())
                .build();
    }

    /**
     * Factory method untuk response error.
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .data(null)
                .timestamp(Instant.now().toString())
                .build();
    }
}
