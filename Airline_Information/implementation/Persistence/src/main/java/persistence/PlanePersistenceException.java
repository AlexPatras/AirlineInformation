package persistence;

public class PlanePersistenceException extends Exception {
    
    public PlanePersistenceException(String message) {
        super(message);
    }

    public PlanePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
