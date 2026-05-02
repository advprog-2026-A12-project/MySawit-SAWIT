package id.ac.ui.cs.advprog.mysawit.garden.exception;

import java.util.UUID;

/**
 * Exception ketika user yang dicari di Auth Service tidak ditemukan atau tidak aktif.
 */
public class UserNotFoundInAuthException extends RuntimeException {

    public UserNotFoundInAuthException(UUID userId) {
        super("User tidak ditemukan di sistem");
    }
}
