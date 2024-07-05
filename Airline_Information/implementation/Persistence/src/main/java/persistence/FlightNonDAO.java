package persistence;

import dataRecords.FlightData;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface FlightNonDAO {
    String getArrivalIATACode(int flightId) throws FlightPersistenceException;
    HashMap<Integer, Integer> getFlightIdsAndPrices(String destination) throws DiscountDatabaseException;
    Map<Integer, List<String>> checkFlightForDiscount(int flightId) throws DiscountDatabaseException;
    List<String> getFlightDestinations() throws DiscountDatabaseException;
    HashMap<Integer, List<String>> checkIfFlightHasDiscount(int flightId) throws DiscountDatabaseException;
    HashMap<Integer, List<String>> getSingleFlightInfo(int flightId) throws DiscountDatabaseException;
    boolean CheckRegisterFlights(FlightData flightData, DataSource ds) throws FlightPersistenceException;
    List<FlightData> getAllFlights() throws FlightPersistenceException;
    ArrayList<String> getIATACode() throws FlightPersistenceException;
    ArrayList<String> getPlaneDetails() throws FlightPersistenceException;
    List<FlightData> getFlight(String departure, String arrival, Date date) throws FlightPersistenceException;
}
