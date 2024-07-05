package persistence;

public class ExtrasPersistenceException extends Exception {

    public ExtrasPersistenceException(String message) {
        super(message);
    }

    public ExtrasPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
