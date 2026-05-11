package id.ac.ui.cs.advprog.mysawit.garden.exception;

public class SupirAlreadyAssignedException extends RuntimeException {

    public SupirAlreadyAssignedException(String supirName) {
        super("Supir '" + supirName + "' sudah ditugaskan ke kebun aktif lain");
    }
}
