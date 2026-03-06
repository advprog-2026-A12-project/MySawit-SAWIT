package id.ac.ui.cs.advprog.mysawit.garden.exception;

import java.util.UUID;

public class KebunHasMandorException extends RuntimeException {

    public KebunHasMandorException(UUID kebunId) {
        super("Tidak dapat menghapus kebun " + kebunId
                + " karena masih memiliki Mandor yang ditugaskan");
    }
}
