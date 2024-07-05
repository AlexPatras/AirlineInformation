package businessLogic;

public class FlightBusinessException extends Exception {

    public FlightBusinessException(String message) {
        super(message);
    }

    public FlightBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
