package persistence;

import java.sql.SQLException;
import java.util.HashMap;

public interface BookingNonDAO {
    int getInsertedBookingId() throws BookingPersistenceException;

    HashMap<Integer, Integer> getBookingIdsAndPrices(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException;
    void updateBookingPrices(HashMap<Integer, Integer> bookingAndFlightIds,
                             HashMap<Integer, Integer> flightIdsAndPrices,
                             HashMap<Integer, Integer> bookingIdsAndPrices,
                             double discountPercentage) throws DiscountDatabaseException;

    void calculateSingleBookingPrice(int bookingId, int newPrice) throws DiscountDatabaseException;
    int getDefaultBookingPrice(int bookingId) throws BookingPersistenceException;
}
