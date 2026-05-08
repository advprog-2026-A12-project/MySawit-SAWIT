package id.ac.ui.cs.advprog.mysawit.garden.exception;

public class KebunHasMandorException extends RuntimeException {

    public KebunHasMandorException(String kebunNama) {
        super("Tidak dapat menghapus kebun '" + kebunNama
                + "' karena masih memiliki Mandor yang ditugaskan");
    }
}
