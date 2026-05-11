package id.ac.ui.cs.advprog.mysawit.garden.exception;

public class MandorAlreadyAssignedException extends RuntimeException {

    public MandorAlreadyAssignedException(String mandorName) {
        super("Mandor '" + mandorName + "' sudah ditugaskan ke kebun aktif lain");
    }
}
