package use_case;

/**
 * Thrown by data access classes when stored data can't be read or saved
 * (for example, the database can't be reached).
 * Unchecked, so one data access class can implement several use cases' interfaces.
 * Interactors should catch it and show a friendly error.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
