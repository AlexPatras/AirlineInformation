package businessLogic;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import persistence.BookingPersistenceException;
import persistence.DiscountDatabaseException;

public interface DiscountManager {

    void applyDiscountToLocation(String destination, String discountMode) throws DiscountNotAppliedException, DiscountDatabaseException;

    void applyDiscountToFlight(int flightId, String discountMode) throws DiscountDatabaseException, DiscountNotAppliedException;

    void staticApplyDiscountToFlight(int flightId, String discountMode) throws DiscountDatabaseException, DiscountNotAppliedException;

    void calculatePrice(int bookingId, int flightId) throws DiscountDatabaseException, BookingPersistenceException, DiscountNotAppliedException;

    List<String> getFlightLocations() throws DiscountDatabaseException;
}
