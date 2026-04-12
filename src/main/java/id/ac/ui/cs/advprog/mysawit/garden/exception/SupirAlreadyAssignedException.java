package id.ac.ui.cs.advprog.mysawit.garden.exception;

import java.util.UUID;

public class SupirAlreadyAssignedException extends RuntimeException {
    public SupirAlreadyAssignedException(UUID supirId) {
        super("Supir dengan ID " + supirId + " sudah ditugaskan ke kebun aktif lain");
    }
}
