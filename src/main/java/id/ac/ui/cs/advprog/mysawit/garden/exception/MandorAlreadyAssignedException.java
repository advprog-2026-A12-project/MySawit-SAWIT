package id.ac.ui.cs.advprog.mysawit.garden.exception;

import java.util.UUID;

public class MandorAlreadyAssignedException extends RuntimeException {
    public MandorAlreadyAssignedException(UUID mandorId) {
        super("Mandor dengan ID " + mandorId + " sudah ditugaskan ke kebun aktif lain");
    }
}
