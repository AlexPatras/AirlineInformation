package persistence;

public class PassengerPersistenceException extends Exception {
    
    public PassengerPersistenceException(String message) {
        super(message);
    }

    public PassengerPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
