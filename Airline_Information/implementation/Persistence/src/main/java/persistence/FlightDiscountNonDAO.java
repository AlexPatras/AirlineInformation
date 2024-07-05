package persistence;

import java.util.HashMap;

public interface FlightDiscountNonDAO {
    void updateCurrentDiscounts(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException;
}
