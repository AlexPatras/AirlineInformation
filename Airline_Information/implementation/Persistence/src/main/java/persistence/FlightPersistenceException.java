package persistence;

public class FlightPersistenceException extends Exception {

    public FlightPersistenceException(String message) {
        super(message);
    }

    public FlightPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
