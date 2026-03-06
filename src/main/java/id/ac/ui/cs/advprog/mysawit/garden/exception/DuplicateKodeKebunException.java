package id.ac.ui.cs.advprog.mysawit.garden.exception;

public class DuplicateKodeKebunException extends RuntimeException {

    public DuplicateKodeKebunException(String kode) {
        super("Kode kebun '" + kode + "' sudah digunakan");
    }
}
