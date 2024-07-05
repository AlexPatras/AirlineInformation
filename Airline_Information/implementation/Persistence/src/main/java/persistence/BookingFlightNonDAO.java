package persistence;

import java.util.HashMap;

public interface BookingFlightNonDAO {
    HashMap<Integer, Integer> getBookingAndFlightIds(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException;
}