package businessLogic;

public class PassengerBusinessException extends Exception {
    
    public PassengerBusinessException(String message) {
        super(message);
    }

    public PassengerBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
