package id.ac.ui.cs.advprog.mysawit.garden.exception;

import java.util.UUID;

public class KebunNotFoundException extends RuntimeException {

    public KebunNotFoundException(UUID kebunId) {
        super("Kebun dengan id " + kebunId + " tidak ditemukan");
    }
}
