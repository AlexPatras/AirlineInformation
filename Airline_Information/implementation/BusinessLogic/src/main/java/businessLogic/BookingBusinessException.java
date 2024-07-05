package businessLogic;

public class BookingBusinessException extends Exception {

    public BookingBusinessException(String message) {
        super(message);
    }

    public BookingBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
