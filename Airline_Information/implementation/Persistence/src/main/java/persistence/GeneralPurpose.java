package persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataRecords.ExtrasData;
import dataRecords.PassengerData;
import persistence.DiscountDatabaseException;

public interface GeneralPurpose {
    Map<Integer, List<String>> checkFlightForDiscount(int flightId) throws DiscountDatabaseException;
    List<String> getFlightDestinations() throws DiscountDatabaseException;
    HashMap<Integer, Integer> getFlightIdsAndPrices(String destination) throws DiscountDatabaseException;
    HashMap<Integer, Integer> getBookingIdsAndPrices(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException;
    HashMap<Integer, Integer> getBookingAndFlightIds(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException;
    void updateBookingPrices (HashMap<Integer, Integer> bookingAndFlightIds,
                              HashMap<Integer, Integer> flightIdsAndPrices,
                              HashMap<Integer, Integer> bookingIdsAndPrices,
                              double discountPercentage) throws DiscountDatabaseException;

    void updateCurrentDiscounts(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException;
}
