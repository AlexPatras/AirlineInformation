package persistence;

public class BookingPersistenceException extends Exception {

    public BookingPersistenceException(String message) {
        super(message);
    }

    public BookingPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
