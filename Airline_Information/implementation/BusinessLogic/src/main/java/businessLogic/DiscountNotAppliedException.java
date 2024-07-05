package businessLogic;

public class DiscountNotAppliedException extends Exception {
    public DiscountNotAppliedException(String errorMessage) {
        super(errorMessage);
    }
}
