package id.ac.ui.cs.advprog.mysawit.garden.exception;

/**
 * Exception ketika user yang diberikan tidak memiliki role yang diharapkan.
 */
public class InvalidUserRoleException extends RuntimeException {

    public InvalidUserRoleException(String expectedRole, String actualRole) {
        super("User memiliki role " + actualRole + ", diharapkan " + expectedRole);
    }
}
