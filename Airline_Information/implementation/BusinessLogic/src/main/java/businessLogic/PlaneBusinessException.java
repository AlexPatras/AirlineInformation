package businessLogic;

public class PlaneBusinessException extends Exception {
    
    public PlaneBusinessException(String message) {
        super(message);
    }

    public PlaneBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
