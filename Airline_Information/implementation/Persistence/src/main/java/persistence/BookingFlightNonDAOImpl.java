package persistence;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingFlightNonDAOImpl implements BookingFlightNonDAO{

    DataSource db = DBController.getDataSource("db");


    /**
     * Retrieves booking and flight ids from "booingFlight" table (based on flight
     * ids present in passed map - only those bookings that have
     * flight id assigned will be selected) and stores results in a map
     * @param flightIdsAndPrices determines which booking and flight ids will be selected
     * @return map of integers which contains booking id as the key and flight id as value
     * @throws DiscountDatabaseException when sql exception is caught
     */
    @Override
    public HashMap<Integer, Integer> getBookingAndFlightIds(HashMap<Integer, Integer> flightIdsAndPrices)
            throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();

            List<Integer> flightIds = new ArrayList<>();
            for (Map.Entry<Integer, Integer> e : flightIdsAndPrices.entrySet()) {
                flightIds.add(e.getKey());
            }

            String selectBookingAndFlightIdsSQL = "SELECT bookingid, flightid FROM bookingFlight WHERE flightid IN ("
                    + generateQuestionMarks(flightIds.size()) + ")";
            PreparedStatement selectBookingAndFlightIdsPS = conn.prepareStatement(selectBookingAndFlightIdsSQL);
            int parameterIndex = 1;
            for (Integer id : flightIds) {
                selectBookingAndFlightIdsPS.setInt(parameterIndex, id);
                parameterIndex++;
            }
            ResultSet selectBookingAndFlightIdsRS = selectBookingAndFlightIdsPS.executeQuery();

            HashMap<Integer, Integer> bookingAndFlightIds = new HashMap<>();

            while (selectBookingAndFlightIdsRS.next()) {
                bookingAndFlightIds.put(selectBookingAndFlightIdsRS.getInt("bookingid"),
                        selectBookingAndFlightIdsRS.getInt("flightid"));
            }

            selectBookingAndFlightIdsRS.close();
            conn.close();
            return bookingAndFlightIds;
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }
    }





    /**
     * Helper method that generates certain amount of question marks
     * to later be put in prepared statement
     * @param howMany determines how many question marks will be generated
     * @return string of question marks, separated by commas.
     */
    private String generateQuestionMarks(int howMany) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < howMany; i++) {
            sb.append("?");
            if (i < howMany - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
