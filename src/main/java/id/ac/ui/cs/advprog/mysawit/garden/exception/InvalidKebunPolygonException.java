package id.ac.ui.cs.advprog.mysawit.garden.exception;

public class InvalidKebunPolygonException extends RuntimeException {
    public InvalidKebunPolygonException() {
        super("Koordinat kebun tidak membentuk polygon yang valid");
    }
}
