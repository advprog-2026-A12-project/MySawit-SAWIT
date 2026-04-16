package id.ac.ui.cs.advprog.mysawit.garden.exception;

public class KebunOverlapException extends RuntimeException {
    public KebunOverlapException() {
        super("Koordinat kebun overlap dengan kebun aktif lain");
    }
}
