package businessLogic;

public class InvalidInputBusinessException extends Exception {
    public InvalidInputBusinessException(String message) {
        super(message);
    }

    public InvalidInputBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
